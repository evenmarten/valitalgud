package ee.bcs.valitalgud.controller.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Ostukorvi vastus koos toodete ja kokkuvõttega")
public class CartResponseDto {

    @Schema(description = "Ostukorvi ID", example = "3")
    private Integer cartId;

    @Schema(description = "Ostukorvis olevad tooted")
    private List<CartItemViewDto> items;

    @Schema(description = "Vahesumma (ilma tarne ja käibemaksuta)", example = "101.49")
    private BigDecimal subtotal;

    @Schema(description = "Tarnetasu", example = "5.00")
    private BigDecimal shipping;

    @Schema(description = "Summas sisalduv käibemaks (24%) — hinnad on käibemaksuga, KM ei lisandu", example = "23.15")
    private BigDecimal tax;

    @Schema(description = "Kogusumma", example = "114.61")
    private BigDecimal total;
}