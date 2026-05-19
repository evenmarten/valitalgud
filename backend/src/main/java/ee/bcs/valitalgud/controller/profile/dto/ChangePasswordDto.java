package ee.bcs.valitalgud.controller.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Parooli vahetamise andmed")
public class ChangePasswordDto {

    @Schema(description = "Praegune parool", example = "vanaParool123")
    private String oldPassword;

    @Schema(description = "Uus parool (min 8 tähemärki)", example = "uusParool456")
    private String newPassword;

    @Schema(description = "Uue parooli kinnitus", example = "uusParool456")
    private String confirmNewPassword;
}
