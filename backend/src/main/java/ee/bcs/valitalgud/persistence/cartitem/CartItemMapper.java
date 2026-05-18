package ee.bcs.valitalgud.persistence.cartitem;

import ee.bcs.valitalgud.controller.cart.dto.CartItemResponseDto;
import ee.bcs.valitalgud.controller.cart.dto.CartItemViewDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CartItemMapper {

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "product.id", target = "productId")
    CartItemResponseDto toCartItemResponseDto(CartItem cartItem);

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    @Mapping(source = "product.price", target = "price")
    @Mapping(expression = "java(cartItem.getProduct().getPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())))", target = "lineTotal")
    CartItemViewDto toCartItemViewDto(CartItem cartItem);

    List<CartItemViewDto> toCartItemViewDtos(List<CartItem> cartItems);
}