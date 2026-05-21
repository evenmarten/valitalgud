package ee.bcs.valitalgud.controller.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Üks vestluse sõnum")
public class ChatMessageDto {

    @Schema(description = "Sõnumi roll", example = "user", allowableValues = {"user", "assistant"})
    private String role;

    @Schema(description = "Sõnumi sisu", example = "Tere!")
    private String content;
}
