package ee.bcs.valitalgud.controller.county;

import ee.bcs.valitalgud.controller.county.dto.CountyResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.CountyService;
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
@Tag(name = "Counties", description = "Maakondade loend dropdown valikuks")
public class CountyController {

    private final CountyService countyService;

    @GetMapping(value = "/counties", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta maakonnad", description = "Tagastab kõik andmebaasis olevad maakonnad sündmuse loomise/muutmise vormi ja filtrite dropdown valikuks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maakonnad edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CountyResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<CountyResponseDto> findCounties() {
        return countyService.findCounties();
    }
}