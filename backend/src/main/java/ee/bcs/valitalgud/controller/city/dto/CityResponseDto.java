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
}
