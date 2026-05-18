package ee.bcs.valitalgud.controller.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Toote lühiandmed (toodete ruudustiku jaoks)")
public class ProductResponseDto {

    @Schema(description = "Toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Toote nimi", example = "Water Bottle")
    private String name;

    @Schema(description = "Toote hind", example = "15.99")
    private BigDecimal price;

    @Schema(description = "Toote pildi URL", example = "/images/products/water-bottle.png")
    private String imageUrl;
}