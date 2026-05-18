package ee.bcs.valitalgud.controller.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ostukorvi elemendi vastus")
public class CartItemResponseDto {

    @Schema(description = "Ostukorvi elemendi ID", example = "10")
    private Integer cartItemId;

    @Schema(description = "Ostukorvi ID", example = "3")
    private Integer cartId;

    @Schema(description = "Toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Kogus", example = "2")
    private Integer quantity;
}