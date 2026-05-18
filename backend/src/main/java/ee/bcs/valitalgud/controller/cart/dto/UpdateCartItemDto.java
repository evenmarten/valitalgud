package ee.bcs.valitalgud.controller.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ostukorvi rea uuendamise päring")
public class UpdateCartItemDto {

    @Schema(description = "Sisseloginud kasutaja ID (õiguste kontrolliks)", example = "1")
    private Integer userId;

    @Schema(description = "Uus kogus (peab olema > 0)", example = "3")
    private Integer quantity;
}