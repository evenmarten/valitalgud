package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.order.dto.CreateOrderDto;
import ee.bcs.valitalgud.controller.order.dto.OrderItemDto;
import ee.bcs.valitalgud.controller.order.dto.OrderResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.persistence.billing.Billing;
import ee.bcs.valitalgud.persistence.billing.BillingRepository;
import ee.bcs.valitalgud.persistence.order.Order;
import ee.bcs.valitalgud.persistence.order.OrderMapper;
import ee.bcs.valitalgud.persistence.order.OrderRepository;
import ee.bcs.valitalgud.persistence.orderitem.OrderItem;
import ee.bcs.valitalgud.persistence.orderitem.OrderItemRepository;
import ee.bcs.valitalgud.persistence.product.Product;
import ee.bcs.valitalgud.persistence.product.ProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final BigDecimal SHIPPING_COST = new BigDecimal("5.00");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.24");

    private final BillingRepository billingRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto createOrderDto) {
        validateOrderFields(createOrderDto);
        validateItemsNotEmpty(createOrderDto.getItems());
        List<Product> products = loadAndValidateProducts(createOrderDto.getItems());
        Billing billing = createAndSaveBilling(createOrderDto);
        BigDecimal subtotal = calculateSubtotal(createOrderDto.getItems(), products);
        BigDecimal shipping = SHIPPING_COST;
        BigDecimal total = subtotal.add(shipping);
        BigDecimal tax = calculateIncludedTax(total);
        Order order = createAndSaveOrder(createOrderDto.getUserId(), billing.getId(), subtotal, shipping, tax, total);
        createOrderItemsAndDecrementStock(createOrderDto.getItems(), products, order);
        return orderMapper.toOrderResponseDto(order);
    }

    private void validateOrderFields(CreateOrderDto createOrderDto) {
        if (isBlank(createOrderDto.getFirstName()) || isBlank(createOrderDto.getLastName())
                || isBlank(createOrderDto.getCountry()) || isBlank(createOrderDto.getStreet())
                || isBlank(createOrderDto.getPostalCode()) || isBlank(createOrderDto.getCity())
                || isBlank(createOrderDto.getPhone()) || isBlank(createOrderDto.getEmail())) {
            throw new BadRequestException(ErrorResponse.MISSING_ORDER_FIELDS);
        }
        if (!EMAIL_PATTERN.matcher(createOrderDto.getEmail()).matches()) {
            throw new BadRequestException(ErrorResponse.INVALID_ORDER_FIELD_FORMAT);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void validateItemsNotEmpty(List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new BadRequestException(ErrorResponse.EMPTY_CART);
        }
    }

    private List<Product> loadAndValidateProducts(List<OrderItemDto> items) {
        List<Product> products = new ArrayList<>();
        for (OrderItemDto orderItemDto : items) {
            Product product = productRepository.findById(orderItemDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(ErrorResponse.PRODUCT_NOT_FOUND));
            validateQuantity(orderItemDto.getQuantity(), product.getStockQuantity());
            products.add(product);
        }
        return products;
    }

    private void validateQuantity(Integer quantity, Integer stockQuantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(ErrorResponse.INVALID_QUANTITY);
        }
        if (quantity > stockQuantity) {
            throw new BadRequestException(ErrorResponse.INSUFFICIENT_STOCK);
        }
    }

    private Billing createAndSaveBilling(CreateOrderDto createOrderDto) {
        Billing billing = new Billing();
        billing.setFirstName(createOrderDto.getFirstName());
        billing.setLastName(createOrderDto.getLastName());
        billing.setCompany(createOrderDto.getCompanyName());
        billing.setCountry(createOrderDto.getCountry());
        billing.setStreet(createOrderDto.getStreet());
        billing.setPostalCode(createOrderDto.getPostalCode());
        billing.setCity(createOrderDto.getCity());
        billing.setPhone(createOrderDto.getPhone());
        billing.setEmail(createOrderDto.getEmail());
        return billingRepository.save(billing);
    }

    // Hinnad sisaldavad käibemaksu, seega KM ei lisandu — see arvutab brutosummas
    // juba sisalduva käibemaksu osa: bruto × määr / (1 + määr).
    private BigDecimal calculateIncludedTax(BigDecimal grossAmount) {
        return grossAmount.multiply(TAX_RATE)
                .divide(BigDecimal.ONE.add(TAX_RATE), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSubtotal(List<OrderItemDto> items, List<Product> products) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            BigDecimal lineTotal = products.get(i).getPrice()
                    .multiply(new BigDecimal(items.get(i).getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private Order createAndSaveOrder(Integer userId, Integer billingId, BigDecimal subtotal,
            BigDecimal shipping, BigDecimal tax, BigDecimal total) {
        Order order = new Order();
        order.setUserId(userId);
        order.setBillingId(billingId);
        order.setStatus("PENDING");
        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setTax(tax);
        order.setTotal(total);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private void createOrderItemsAndDecrementStock(List<OrderItemDto> items, List<Product> products, Order order) {
        for (int i = 0; i < items.size(); i++) {
            createAndSaveOrderItem(items.get(i), products.get(i), order);
            decrementStock(products.get(i), items.get(i).getQuantity());
        }
    }

    private void createAndSaveOrderItem(OrderItemDto orderItemDto, Product product, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());
        orderItemRepository.save(orderItem);
    }

    private void decrementStock(Product product, Integer quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }
}