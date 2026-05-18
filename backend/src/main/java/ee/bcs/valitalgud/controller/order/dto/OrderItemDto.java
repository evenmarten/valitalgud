package ee.bcs.valitalgud.controller.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Tellimuse rea andmed")
public class OrderItemDto {

    @Schema(description = "Toote ID", example = "1")
    private Integer productId;

    @Schema(description = "Kogus", example = "2")
    private Integer quantity;
}