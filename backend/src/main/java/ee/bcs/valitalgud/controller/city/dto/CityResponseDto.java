package ee.bcs.valitalgud.controller.city.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Linna andmed (dropdown valikuks)")
public class CityResponseDto {

    @Schema(description = "Linna ID", example = "1")
    private Integer id;

    @Schema(description = "Linna nimi", example = "Tallinn")
    private String name;

    @Schema(description = "Maakonna ID, kuhu linn kuulub", example = "1")
    private Integer countyId;

    @Schema(description = "Maakonna nimi, kuhu linn kuulub", example = "Harju maakond")
    private String county;
}
