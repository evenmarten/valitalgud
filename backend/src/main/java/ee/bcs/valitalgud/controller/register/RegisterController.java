package ee.bcs.valitalgud.controller.register;

import ee.bcs.valitalgud.controller.login.dto.LoginResponseDto;
import ee.bcs.valitalgud.controller.register.dto.RegisterDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.RegisterService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Register", description = "Uue kasutaja registreerimine")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registreeri uus kasutaja", description = "Loob uue konto täisnime, e-posti ja parooliga. Tagastab loodud kasutaja andmed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kasutaja loodud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Kohustuslikud väljad puuduvad (MISSING_FIELDS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "E-post on juba kasutusel (EMAIL_ALREADY_EXISTS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterDto registerDto) {
        LoginResponseDto response = registerService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
