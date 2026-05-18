package ee.bcs.valitalgud.controller.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ostukorvi lisamise päring")
public class AddCartItemDto {

    @Schema(description = "Sisseloginud kasutaja ID", example = "1")
    private Integer userId;

    @Schema(description = "Lisatava toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Kogus (peab olema > 0)", example = "2")
    private Integer quantity;
}