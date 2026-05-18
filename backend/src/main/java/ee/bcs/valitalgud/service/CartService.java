package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.cart.dto.AddCartItemDto;
import ee.bcs.valitalgud.controller.cart.dto.CartItemResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.BadRequestException;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.cart.Cart;
import ee.bcs.valitalgud.persistence.cart.CartRepository;
import ee.bcs.valitalgud.persistence.cartitem.CartItem;
import ee.bcs.valitalgud.persistence.cartitem.CartItemMapper;
import ee.bcs.valitalgud.persistence.cartitem.CartItemRepository;
import ee.bcs.valitalgud.persistence.product.Product;
import ee.bcs.valitalgud.persistence.product.ProductRepository;
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

    @Transactional
    public CartItemResponseDto addCartItem(AddCartItemDto dto) {
        if (dto.getUserId() == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
        if (dto.getProductId() == null) {
            throw new BadRequestException(ErrorResponse.MISSING_FIELDS);
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException(ErrorResponse.INVALID_QUANTITY);
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException(ErrorResponse.PRODUCT_NOT_FOUND));

        if (dto.getQuantity() > product.getStockQuantity()) {
            throw new BadRequestException(ErrorResponse.INSUFFICIENT_STOCK);
        }

        Cart cart = cartRepository.findByUserId(dto.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(dto.getUserId());
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + dto.getQuantity());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(dto.getQuantity());
                    return cartItemRepository.save(newItem);
                });

        return cartItemMapper.toCartItemResponseDto(cartItem);
    }
}