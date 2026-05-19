package ee.bcs.valitalgud.controller.registration;

import ee.bcs.valitalgud.controller.registration.dto.RegistrationDto;
import ee.bcs.valitalgud.controller.registration.dto.RegistrationResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Registrations", description = "Sündmusele registreerumine")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(value = "/events/{eventId}/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registreeru sündmusele või muuda staatust",
            description = "Esmaregistreerimisel tagastab 201 Created. Olemasoleva registreerumise staatuse muutmisel tagastab 200 OK.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registreerumine loodud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegistrationResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "Registreerumise staatus uuendatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegistrationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Vale staatus (INVALID_REGISTRATION_STATUS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Sündmus täis või tühistatud (EVENT_FULL / EVENT_CANCELLED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<RegistrationResponseDto> register(
            @PathVariable Integer eventId,
            @RequestParam Integer userId,
            @RequestBody RegistrationDto registrationDto) {
        boolean isNew = registrationService.isNewRegistration(eventId, userId);
        RegistrationResponseDto response = registrationService.register(eventId, userId, registrationDto);
        HttpStatus status = isNew ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(value = "/events/{eventId}/register")
    @Operation(summary = "Tühista registreerumine", description = "Kustutab kasutaja registreerumise sündmuselt. Tagastab 204 ka siis, kui registreerumist polnud.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registreerumine tühistatud"),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable Integer eventId,
            @RequestParam Integer userId) {
        registrationService.cancelRegistration(eventId, userId);
        return ResponseEntity.noContent().build();
    }
}
