package ee.bcs.valitalgud.controller.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sisselogimise vastus")
public class LoginResponseDto {

    @Schema(description = "Kasutaja ID", example = "1")
    private Long userId;

    @Schema(description = "Eesnimi", example = "Mari")
    private String firstName;

    @Schema(description = "Perekonnanimi", example = "Maasikas")
    private String lastName;

    @Schema(description = "Kasutaja roll", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
}
