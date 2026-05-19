package ee.bcs.valitalgud.controller.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Uue kasutaja registreerimise päring")
public class RegisterDto {

    @Schema(description = "Kasutaja täisnimi", example = "Mari Maasikas")
    private String fullName;

    @Schema(description = "Kasutaja e-post", example = "mari.maasikas@ettevote.ee")
    private String email;

    @Schema(description = "Kasutaja parool (vähemalt 8 tähemärki)", example = "password123")
    private String password;

    @Schema(description = "Telefoninumber (vabatahtlik)", example = "+372 555 1234", nullable = true)
    private String phone;

    @Schema(description = "Lühike kirjeldus kasutaja kohta (vabatahtlik)", example = "Räägi endast paar sõna...", nullable = true)
    private String description;
}
