package ee.bcs.valitalgud.controller.myorganizedevents;

import ee.bcs.valitalgud.controller.event.dto.OrganizedEventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.MyOrganizedEventsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "MyOrganizedEvents", description = "Sisselogitud kasutaja korraldatud sündmuste loend")
public class MyOrganizedEventsController {

    private final MyOrganizedEventsService myOrganizedEventsService;

    @GetMapping(value = "/my-organized-events", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta minu korraldatud sündmused",
            description = "Tagastab sisselogitud kasutaja korraldatud sündmused tabelivormingus. Toetab linna, maakonna, oskuse-tagi ja kuupäeva filtreid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmused edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = OrganizedEventResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Kasutaja ID puudub (MISSING_USER_ID) või vigane kuupäev (INVALID_QUERY_PARAMETER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<OrganizedEventResponseDto> findMyOrganizedEvents(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Integer countyId,
            @RequestParam(required = false) Integer skillTagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return myOrganizedEventsService.findMyOrganizedEvents(userId, cityId, countyId, skillTagId, date);
    }
}
