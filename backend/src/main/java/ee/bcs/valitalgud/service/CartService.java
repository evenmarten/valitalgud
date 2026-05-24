package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.cart.dto.AddCartItemDto;
import ee.bcs.valitalgud.controller.cart.dto.CartItemResponseDto;
import ee.bcs.valitalgud.controller.cart.dto.CartItemViewDto;
import ee.bcs.valitalgud.controller.cart.dto.CartResponseDto;
import ee.bcs.valitalgud.controller.cart.dto.UpdateCartItemDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.ForbiddenException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.cart.Cart;
import ee.bcs.valitalgud.persistence.cart.CartRepository;
import ee.bcs.valitalgud.persistence.cartitem.CartItem;
import ee.bcs.valitalgud.persistence.cartitem.CartItemMapper;
import ee.bcs.valitalgud.persistence.cartitem.CartItemRepository;
import ee.bcs.valitalgud.persistence.product.Product;
import ee.bcs.valitalgud.persistence.product.ProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;
    private final UserValidationService userValidationService;

    @Transactional
    public CartItemResponseDto addCartItem(AddCartItemDto dto) {
        validateAddCartItemFields(dto);
        Product product = getValidProductBy(dto.getProductId());
        validateQuantity(dto.getQuantity(), product.getStockQuantity());
        Cart cart = getOrCreateCart(dto.getUserId());
        CartItem cartItem = addOrUpdateCartItem(cart, product, dto.getQuantity());
        return cartItemMapper.toCartItemResponseDto(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(Integer userId) {
        validateUserId(userId);
        return cartRepository.findByUserId(userId)
                .map(this::buildCartResponse)
                .orElseGet(this::buildEmptyCartResponse);
    }

    @Transactional
    public CartResponseDto updateCartItem(Integer cartItemId, UpdateCartItemDto dto) {
        validateUserId(dto.getUserId());
        CartItem cartItem = getValidCartItemBy(cartItemId);
        validateCartOwnership(cartItem, dto.getUserId());
        validateQuantity(dto.getQuantity(), cartItem.getProduct().getStockQuantity());
        cartItem.setQuantity(dto.getQuantity());
        cartItemRepository.save(cartItem);
        return buildCartResponse(cartItem.getCart());
    }

    @Transactional
    public CartResponseDto deleteCartItem(Integer cartItemId, Integer userId) {
        validateUserId(userId);
        CartItem cartItem = getValidCartItemBy(cartItemId);
        validateCartOwnership(cartItem, userId);
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart);
    }

    private void validateAddCartItemFields(AddCartItemDto dto) {
        if (dto.getUserId() == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
        userValidationService.ensureExistsAndActive(dto.getUserId());
        if (dto.getProductId() == null) {
            throw new BadRequestException(ErrorResponse.MISSING_FIELDS);
        }
    }

    private Product getValidProductBy(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.PRODUCT_NOT_FOUND));
    }

    private Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    private CartItem addOrUpdateCartItem(Cart cart, Product product, Integer quantity) {
        return cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(quantity);
                    return cartItemRepository.save(newItem);
                });
    }

    private CartResponseDto buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemViewDto> itemDtos = cartItemMapper.toCartItemViewDtos(items);
        BigDecimal subtotal = calculateSubtotal(itemDtos);
        BigDecimal shipping = itemDtos.isEmpty() ? BigDecimal.ZERO : new BigDecimal("5.00");
        BigDecimal total = subtotal.add(shipping);
        // Hinnad sisaldavad käibemaksu — KM ei lisandu, vaid on summas sees: bruto × 24 / 124.
        BigDecimal tax = total.multiply(new BigDecimal("0.24"))
                .divide(new BigDecimal("1.24"), 2, RoundingMode.HALF_UP);
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setCartId(cart.getId());
        cartResponseDto.setItems(itemDtos);
        cartResponseDto.setSubtotal(subtotal);
        cartResponseDto.setShipping(shipping);
        cartResponseDto.setTax(tax);
        cartResponseDto.setTotal(total);
        return cartResponseDto;
    }

    private CartResponseDto buildEmptyCartResponse() {
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setItems(List.of());
        cartResponseDto.setSubtotal(BigDecimal.ZERO);
        cartResponseDto.setShipping(BigDecimal.ZERO);
        cartResponseDto.setTax(BigDecimal.ZERO);
        cartResponseDto.setTotal(BigDecimal.ZERO);
        return cartResponseDto;
    }

    private BigDecimal calculateSubtotal(List<CartItemViewDto> items) {
        return items.stream()
                .map(CartItemViewDto::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartItem getValidCartItemBy(Integer cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.CART_ITEM_NOT_FOUND));
    }

    private void validateCartOwnership(CartItem cartItem, Integer userId) {
        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new ForbiddenException(ErrorResponse.NOT_CART_OWNER);
        }
    }

    private void validateQuantity(Integer quantity, Integer stockQuantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(ErrorResponse.INVALID_QUANTITY);
        }
        if (quantity > stockQuantity) {
            throw new BadRequestException(ErrorResponse.INSUFFICIENT_STOCK);
        }
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BadRequestException(ErrorResponse.MISSING_USER_ID);
        }
        userValidationService.ensureExistsAndActive(userId);
    }
}