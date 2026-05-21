package ee.bcs.valitalgud.controller.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Vestlusroboti päring")
public class ChatRequestDto {

    @Schema(description = "Kasutaja uus sõnum", example = "Mis on valitalgud?")
    private String message;

    @Schema(description = "Senise vestluse ajalugu (valikuline, vanim esimesena)")
    private List<ChatMessageDto> history;
}
