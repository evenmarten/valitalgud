package ee.bcs.valitalgud.controller.county.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Maakonna andmed (dropdown valikuks)")
public class CountyResponseDto {

    @Schema(description = "Maakonna ID", example = "1")
    private Integer id;

    @Schema(description = "Maakonna nimi", example = "Harju maakond")
    private String name;
}