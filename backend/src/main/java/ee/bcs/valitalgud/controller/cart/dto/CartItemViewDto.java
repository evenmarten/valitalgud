package ee.bcs.valitalgud.controller.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Ostukorvis oleva toote andmed")
public class CartItemViewDto {

    @Schema(description = "Ostukorvi rea ID", example = "10")
    private Integer cartItemId;

    @Schema(description = "Toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Toote nimi", example = "Water Bottle")
    private String name;

    @Schema(description = "Toote pildi URL", example = "/images/products/water-bottle.jpg")
    private String imageUrl;

    @Schema(description = "Toote ühiku hind", example = "15.99")
    private BigDecimal price;

    @Schema(description = "Kogus", example = "2")
    private Integer quantity;

    @Schema(description = "Rea koguhind (hind × kogus)", example = "31.98")
    private BigDecimal lineTotal;
}