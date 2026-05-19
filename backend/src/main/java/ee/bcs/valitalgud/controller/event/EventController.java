package ee.bcs.valitalgud.controller.event;

import ee.bcs.valitalgud.controller.event.dto.EventDetailsResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.EventService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Events", description = "Sündmuste loend ja filtreerimine")
public class EventController {

    private final EventService eventService;

    @GetMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta sündmuste loend",
            description = "Tagastab kõik aktiivsed (is_cancelled = false) sündmused. Toetab linna, oskuse-tagi ja kuupäeva filtreid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmused edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Vigane filtri parameeter (INVALID_QUERY_PARAMETER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<EventResponseDto> getEvents(
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Integer skillTagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        return eventService.getFilteredEvents(cityId, skillTagId, fromDate);
    }

    @GetMapping(value = "/events/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta sündmuse üksikasjad", description = "Tagastab ühe sündmuse täieliku info koos organisaatori, osalejate arvu ja kasutaja registreerumise staatusega.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmuse andmed tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventDetailsResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public EventDetailsResponseDto getEventDetails(
            @PathVariable Integer eventId,
            @RequestParam Integer userId) {
        return eventService.getEventDetails(eventId, userId);
    }
}
