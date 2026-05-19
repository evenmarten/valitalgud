package ee.bcs.valitalgud.controller.registration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Registreerumise vastus")
public class RegistrationResponseDto {

    @Schema(description = "Registreerumise ID", example = "17")
    private Integer registrationId;

    @Schema(description = "Sündmuse ID", example = "1")
    private Integer eventId;

    @Schema(description = "Kasutaja ID", example = "12")
    private Integer userId;

    @Schema(description = "Registreerumise staatus", example = "LAHEB",
            allowableValues = {"LAHEB", "VOIB_OLLA", "EI_LAHE"})
    private String status;

    @Schema(description = "Registreerumise aeg", example = "2024-01-15T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registeredAt;
}
