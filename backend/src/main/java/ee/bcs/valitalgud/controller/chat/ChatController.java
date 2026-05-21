package ee.bcs.valitalgud.controller.chat;

import ee.bcs.valitalgud.controller.chat.dto.ChatRequestDto;
import ee.bcs.valitalgud.controller.chat.dto.ChatResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Chatbot", description = "AI vestlusrobot (Groq) landing lehe jaoks")
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Saada sõnum AI vestlusrobotile",
            description = "Edastab kasutaja sõnumi koos valikulise vestluse ajalooga Groq AI mudelile ja tagastab vastuse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vastus edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Sõnum puudub (CHAT_MESSAGE_REQUIRED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "502", description = "Groq päring ebaõnnestus (CHAT_REQUEST_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "503", description = "API võti puudub (CHAT_NOT_CONFIGURED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return chatService.chat(request);
    }
}
