package ee.bcs.valitalgud.controller.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Profiili uuendamise andmed")
public class UpdateProfileDto {

    @Schema(description = "Eesnimi", example = "John")
    private String firstName;

    @Schema(description = "Keskmised nimed (valikuline)", example = "William")
    private String middleName;

    @Schema(description = "Perekonnanimi", example = "Doe")
    private String lastName;

    @Schema(description = "E-posti aadress", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Telefoninumber (valikuline)", example = "555-12-4567")
    private String phone;

    @Schema(description = "Kirjeldus (valikuline, eeldus: praegu ignoreeritakse)", example = "Lorem ipsum")
    private String description;
}
