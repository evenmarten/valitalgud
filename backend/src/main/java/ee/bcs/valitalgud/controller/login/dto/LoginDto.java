package ee.bcs.valitalgud.controller.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Sisselogimise päring")
public class LoginDto {

    @Schema(description = "Kasutaja e-post", example = "mari.maasikas@ettevote.ee")
    private String email;

    @Schema(description = "Kasutaja parool", example = "********")
    private String password;
}
