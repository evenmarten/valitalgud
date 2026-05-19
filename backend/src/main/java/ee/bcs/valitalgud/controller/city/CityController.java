package ee.bcs.valitalgud.controller.city;

import ee.bcs.valitalgud.controller.city.dto.CityResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Cities", description = "Linnade loend dropdown valikuks")
public class CityController {

    private final CityService cityService;

    @GetMapping(value = "/cities", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta linnad", description = "Tagastab kõik andmebaasis olevad linnad sündmuse loomise/muutmise vormi dropdown valikuks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Linnad edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CityResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<CityResponseDto> findCities() {
        return cityService.findCities();
    }
}
