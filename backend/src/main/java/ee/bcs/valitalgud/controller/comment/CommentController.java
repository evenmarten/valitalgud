package ee.bcs.valitalgud.controller.comment;

import ee.bcs.valitalgud.controller.comment.dto.CommentResponseDto;
import ee.bcs.valitalgud.controller.comment.dto.CreateCommentDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Comments", description = "Sündmuse kommentaarid")
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "/events/{eventId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Hangi sündmuse kommentaarid", description = "Tagastab kõik kommentaarid sorteeritult uusimad esimesena.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kommentaarid tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<CommentResponseDto> getComments(
            @PathVariable Integer eventId,
            @RequestParam(required = false) Integer userId) {
        return commentService.getComments(eventId, userId);
    }

    @PostMapping(value = "/events/{eventId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lisa kommentaar", description = "Lisab uue kommentaari sündmusele. Kommentaar peab olema maksimaalselt 1000 tähemärki.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kommentaar lisatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Puuduv või liiga pikk sisu (COMMENT_CONTENT_REQUIRED / COMMENT_TOO_LONG)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Integer eventId,
            @RequestParam Integer userId,
            @RequestBody CreateCommentDto createCommentDto) {
        CommentResponseDto response = commentService.addComment(eventId, userId, createCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
