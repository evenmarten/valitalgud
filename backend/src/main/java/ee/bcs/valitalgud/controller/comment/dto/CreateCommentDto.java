package ee.bcs.valitalgud.controller.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Uue kommentaari päring")
public class CreateCommentDto {

    @Schema(description = "Kommentaari sisu (kohustuslik, max 1000 tähemärki)",
            example = "Põnev üritus, tulen kindlasti!")
    private String content;
}
