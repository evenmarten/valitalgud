package ee.bcs.valitalgud.controller.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Vestlusroboti vastus")
public class ChatResponseDto {

    @Schema(description = "AI assistendi vastus")
    private String reply;
}
