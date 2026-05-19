package ee.bcs.valitalgud.controller.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Kuu kalendriandmed koos sündmustega päevadega")
public class CalendarResponseDto {

    @Schema(description = "Kuu number (1–12)", example = "11")
    private Integer month;

    @Schema(description = "Aasta", example = "2023")
    private Integer year;

    @Schema(description = "Päevanumbrid, mil toimub vähemalt üks sündmus", example = "[4, 9, 21, 22]")
    private List<Integer> daysWithEvents;
}
