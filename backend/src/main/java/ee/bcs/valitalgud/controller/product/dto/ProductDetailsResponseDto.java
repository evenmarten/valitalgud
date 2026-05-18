package ee.bcs.valitalgud.controller.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Toote täisandmed (külgpaneeli jaoks)")
public class ProductDetailsResponseDto {

    @Schema(description = "Toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Toote nimi", example = "Generic Product Name")
    private String name;

    @Schema(description = "Toote kirjeldus", example = "A short description of the product.")
    private String description;

    @Schema(description = "Toote hind", example = "15.99")
    private BigDecimal price;

    @Schema(description = "Toote pildi URL", example = "/images/products/water-bottle.png")
    private String imageUrl;

    @Schema(description = "Laoseis", example = "42")
    private Integer stockQuantity;
}