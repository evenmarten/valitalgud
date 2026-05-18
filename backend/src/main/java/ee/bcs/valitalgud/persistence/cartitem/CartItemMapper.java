package ee.bcs.valitalgud.persistence.cartitem;

import ee.bcs.valitalgud.controller.cart.dto.CartItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CartItemMapper {

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "product.id", target = "productId")
    CartItemResponseDto toCartItemResponseDto(CartItem cartItem);
}