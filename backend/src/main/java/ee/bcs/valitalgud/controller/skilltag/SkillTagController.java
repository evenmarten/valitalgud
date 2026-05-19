package ee.bcs.valitalgud.controller.skilltag;

import ee.bcs.valitalgud.controller.skilltag.dto.SkillTagResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.SkillTagService;
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
@Tag(name = "SkillTags", description = "Oskuse-tagide loend multi-select valikuks")
public class SkillTagController {

    private final SkillTagService skillTagService;

    @GetMapping(value = "/skill-tags", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta oskuse-tagid", description = "Tagastab kõik andmebaasis olevad oskuse-tagid sündmuse loomise/muutmise vormi multi-select valikuks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oskuse-tagid edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SkillTagResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<SkillTagResponseDto> findSkillTags() {
        return skillTagService.findSkillTags();
    }
}
