package ee.bcs.valitalgud.controller.event;

import ee.bcs.valitalgud.controller.event.dto.CreateEventDto;
import ee.bcs.valitalgud.controller.event.dto.EventDetailsResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.controller.event.dto.UpdateEventDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Events", description = "Sündmuste loend, loomine, muutmine ja tühistamine")
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
            @RequestParam(required = false) Integer userId) {
        return eventService.getEventDetails(eventId, userId);
    }

    @GetMapping(value = "/events/{eventId}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta sündmuse andmed vormi eeltäitmiseks",
            description = "Kasutusel EditEventView.vue avamisel. Tagastab kõik sündmuse väljad sh skillTagIds, currentParticipants ja isCancelled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmuse andmed tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public EventResponseDto getEventForEdit(
            @PathVariable Integer eventId,
            @RequestParam Integer userId) {
        return eventService.getEventForEdit(eventId, userId);
    }

    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Loo uus sündmus",
            description = "Loob uue sündmuse sisselogitud kasutaja nimel (organizer määratakse query parameetri userId põhjal).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sündmus edukalt loodud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Vigased andmed (INVALID_EVENT_DATA / INVALID_EVENT_DATE / INVALID_EVENT_TIME_RANGE / INVALID_PARTICIPANTS_COUNT / CITY_NOT_FOUND / SKILL_TAG_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<EventResponseDto> createEvent(
            @RequestBody CreateEventDto createEventDto,
            @RequestParam Integer userId) {
        EventResponseDto created = eventService.createEvent(createEventDto, userId);
        URI location = URI.create("/api/events/" + created.getEventId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(value = "/events/{eventId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Uuenda sündmust",
            description = "Uuendab olemasoleva sündmuse andmeid. Lubatud ainult sündmuse loojale või ADMIN-ile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmus uuendatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Vigased andmed (INVALID_EVENT_DATA / INVALID_EVENT_DATE / INVALID_EVENT_TIME_RANGE / MAX_PARTICIPANTS_BELOW_CURRENT / SKILL_TAG_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Kasutaja pole omanik ega ADMIN (NOT_EVENT_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public EventResponseDto updateEvent(
            @PathVariable Integer eventId,
            @RequestBody UpdateEventDto updateEventDto,
            @RequestParam Integer userId) {
        return eventService.updateEvent(eventId, updateEventDto, userId);
    }

    @DeleteMapping(value = "/events/{eventId}")
    @Operation(summary = "Tühista sündmus (soft delete)",
            description = "Märgib sündmuse tühistatuks (is_cancelled = true). Lubatud ainult sündmuse loojale või ADMIN-ile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sündmus tühistatud"),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Kasutaja pole omanik ega ADMIN (NOT_EVENT_OWNER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sündmust ei leitud (EVENT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Integer eventId,
            @RequestParam Integer userId) {
        eventService.deleteEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }
}
