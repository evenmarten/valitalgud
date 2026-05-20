package ee.bcs.valitalgud.controller.myevents;

import ee.bcs.valitalgud.controller.myevents.dto.MyEventResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.MyEventsService;
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
@Tag(name = "MyEvents", description = "Sisselogitud kasutaja registreerunud sündmused")
public class MyEventsController {

    private final MyEventsService myEventsService;

    @GetMapping(value = "/my-events", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta minu registreerunud sündmused",
            description = "Tagastab sisselogitud kasutaja registreerunud sündmused filtreeritult (THIS_WEEK / UPCOMING / ALL_FUTURE). Tühistatud ja möödunud sündmusi ei tagastata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sündmused edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MyEventResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Vigane filter (INVALID_FILTER)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public List<MyEventResponseDto> findMyEvents(
            @RequestParam Integer userId,
            @RequestParam String filter,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Integer skillTagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        return myEventsService.findMyEvents(userId, filter, cityId, skillTagId, fromDate);
    }
}
