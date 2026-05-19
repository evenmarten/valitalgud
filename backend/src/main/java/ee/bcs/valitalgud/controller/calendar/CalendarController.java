package ee.bcs.valitalgud.controller.calendar;

import ee.bcs.valitalgud.controller.calendar.dto.CalendarResponseDto;
import ee.bcs.valitalgud.controller.event.dto.EventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.CalendarService;
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
@Tag(name = "Calendar", description = "Kalendrivaate endpointid")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping(value = "/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Hangi kuu kalendriandmed",
            description = "Tagastab antud kuu ja aasta kohta päevanumbrid, millel toimub vähemalt üks aktiivne sündmus.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kalendriandmed tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CalendarResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Vigased parameetrid (INVALID_CALENDAR_PARAMS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public CalendarResponseDto getCalendar(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam Integer userId) {
        return calendarService.getCalendar(month, year, userId);
    }

    @GetMapping(value = "/calendar/day", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Hangi päeva sündmused",
            description = "Tagastab kõik aktiivsed sündmused antud kuupäeval, sorteeritult algusaja järgi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmused tagastatud (tühi loend kui pole sündmusi)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Vigane kuupäeva formaat (INVALID_DATE_FORMAT)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<EventResponseDto> getDayEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer userId) {
        return calendarService.getDayEvents(date, userId);
    }
}
