package ee.bcs.valitalgud.infrastructure.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API veavastus")
public class ApiError {

    @Schema(description = "Vea kood", example = "INVALID_CREDENTIALS")
    private String code;

    @Schema(description = "Vea sõnum kasutajale", example = "Vale email või parool")
    private String message;
}
