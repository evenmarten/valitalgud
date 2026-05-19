package ee.bcs.valitalgud.controller.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Registreerumise päring")
public class RegistrationDto {

    @Schema(description = "Registreerumise staatus", example = "LAHEB",
            allowableValues = {"LAHEB", "VOIB_OLLA", "EI_LAHE"})
    private String status;
}
