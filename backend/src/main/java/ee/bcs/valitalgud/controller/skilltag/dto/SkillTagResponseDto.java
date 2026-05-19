package ee.bcs.valitalgud.controller.skilltag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Oskuse-tagi andmed (multi-select valikuks)")
public class SkillTagResponseDto {

    @Schema(description = "Oskuse-tagi ID", example = "1")
    private Integer id;

    @Schema(description = "Oskuse-tagi nimi", example = "IT")
    private String name;
}
