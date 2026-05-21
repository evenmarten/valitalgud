package ee.bcs.valitalgud.controller.contact;

import ee.bcs.valitalgud.controller.contact.dto.ContactRequestDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.ContactService;
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
@Tag(name = "Contact", description = "Kontaktivormi päringud (saadetakse meiliga Resend API kaudu)")
public class ContactController {

    private final ContactService contactService;

    @PostMapping(value = "/contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Saada kontaktivormi päring",
            description = "Saadab kontaktivormi sisu meiliga platvormi omaniku aadressile Resend API kaudu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sõnum edukalt saadetud"),
            @ApiResponse(responseCode = "400", description = "Väljad puuduvad või e-mail on vigane (CONTACT_FIELDS_REQUIRED, INVALID_EMAIL_FORMAT)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "502", description = "Resend päring ebaõnnestus (CONTACT_REQUEST_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "503", description = "Kontaktivorm pole seadistatud (CONTACT_NOT_CONFIGURED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public void sendContactMessage(@RequestBody ContactRequestDto request) {
        contactService.sendContactMessage(request);
    }
}
