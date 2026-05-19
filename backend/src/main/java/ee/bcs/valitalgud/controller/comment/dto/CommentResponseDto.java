package ee.bcs.valitalgud.controller.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "Kommentaari andmed")
public class CommentResponseDto {

    @Schema(description = "Kommentaari ID", example = "1")
    private Integer commentId;

    @Schema(description = "Autori nimi", example = "Alice Smith")
    private String authorName;

    @Schema(description = "Autori kasutaja ID", example = "7")
    private Integer authorId;

    @Schema(description = "Kommentaari sisu", example = "Põnev üritus, tulen kindlasti!")
    private String content;

    @Schema(description = "Loomise aeg", example = "2024-01-12T14:25:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
