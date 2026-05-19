package ee.bcs.valitalgud.controller.profile;

import ee.bcs.valitalgud.controller.profile.dto.ChangePasswordDto;
import ee.bcs.valitalgud.controller.profile.dto.ProfileResponseDto;
import ee.bcs.valitalgud.controller.profile.dto.UpdateProfileDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Profile", description = "Kasutaja profiili vaatamine, muutmine ja kustutamine")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(value = "/profile/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Hangi kasutaja profiil",
            description = "Tagastab kasutaja profiili andmed. Lubatud ainult kasutajale endale või ADMIN-ile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiil tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Pole profiili omanik ega ADMIN (NOT_PROFILE_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Kasutajat ei leitud (USER_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ProfileResponseDto getProfile(
            @PathVariable Integer userId,
            @RequestParam Integer requesterId) {
        return profileService.getProfile(userId, requesterId);
    }

    @PutMapping(value = "/profile/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Uuenda kasutaja profiili",
            description = "Uuendab nime, e-posti ja telefoninumbri. Lubatud ainult kasutajale endale.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiil uuendatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Puuduvad kohustuslikud väljad või vigane e-post (MISSING_FIELDS / INVALID_EMAIL_FORMAT)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Pole profiili omanik (NOT_PROFILE_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Kasutajat ei leitud (USER_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "E-post on juba kasutusel (EMAIL_ALREADY_EXISTS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ProfileResponseDto updateProfile(
            @PathVariable Integer userId,
            @RequestParam Integer requesterId,
            @RequestBody UpdateProfileDto updateProfileDto) {
        return profileService.updateProfile(userId, requesterId, updateProfileDto);
    }

    @PutMapping(value = "/profile/{userId}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vaheta parool",
            description = "Kontrollib vana parooli ning salvestab uue. Lubatud ainult kasutajale endale.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parool muudetud"),
            @ApiResponse(responseCode = "400", description = "Vale vana parool / paroolid ei ühti / liiga lühike (WRONG_OLD_PASSWORD / PASSWORDS_DO_NOT_MATCH / PASSWORD_TOO_SHORT)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Pole profiili omanik (NOT_PROFILE_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Kasutajat ei leitud (USER_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> changePassword(
            @PathVariable Integer userId,
            @RequestParam Integer requesterId,
            @RequestBody ChangePasswordDto changePasswordDto) {
        profileService.changePassword(userId, requesterId, changePasswordDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/profile/{userId}")
    @Operation(summary = "Kustuta konto (pehme kustutus)",
            description = "Seab kasutaja staatuseks DELETED. Lubatud ainult kasutajale endale või ADMIN-ile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Konto kustutatud"),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Pole profiili omanik ega ADMIN (NOT_PROFILE_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Kasutajat ei leitud (USER_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Integer userId,
            @RequestParam Integer requesterId) {
        profileService.deleteProfile(userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
