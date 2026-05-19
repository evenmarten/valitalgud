package ee.bcs.valitalgud.controller.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Kasutaja profiili andmed")
public class ProfileResponseDto {

    @Schema(description = "Kasutaja ID", example = "1")
    private Integer userId;

    @Schema(description = "Täisnimi", example = "John Doe")
    private String fullName;

    @Schema(description = "E-posti aadress", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Telefoninumber", example = "555-12-4567")
    private String phone;

    @Schema(description = "Roll", example = "USER")
    private String role;

    @Schema(description = "Kirjeldus (praegu null — DB veerg puudub)", example = "null")
    private String description;
}
