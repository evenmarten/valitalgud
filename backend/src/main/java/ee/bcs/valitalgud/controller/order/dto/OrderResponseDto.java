package ee.bcs.valitalgud.controller.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Loodud tellimuse andmed")
public class OrderResponseDto {

    @Schema(description = "Tellimuse ID", example = "101")
    private Integer orderId;

    @Schema(description = "Tellimuse kogusumma", example = "114.61")
    private BigDecimal totalAmount;

    @Schema(description = "Tellimuse staatus", example = "PENDING")
    private String status;

    @Schema(description = "Loomise aeg (ISO-8601)", example = "2026-05-18T15:30:00")
    private String createdAt;
}