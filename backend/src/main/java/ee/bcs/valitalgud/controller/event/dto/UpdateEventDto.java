package ee.bcs.valitalgud.controller.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Sündmuse uuendamise päring")
public class UpdateEventDto {

    @Schema(description = "Sündmuse pealkiri", example = "Muudetud Sündmus")
    private String title;

    @Schema(description = "Sündmuse kirjeldus", example = "Uuendatud kirjeldus")
    private String description;

    @Schema(description = "Aadress", example = "Muudetud tänav 1")
    private String address;

    @Schema(description = "Kuupäev", example = "2026-12-15")
    private LocalDate date;

    @Schema(description = "Algusaeg", example = "11:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "Lõpuaeg", example = "13:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "Maksimaalne osalejate arv (peab olema >= currentParticipants)", example = "60")
    private Integer maxParticipants;

    @Schema(description = "Oskuse-tagide ID-d", example = "[1, 3]")
    private List<Integer> skillTagIds;

    @Schema(description = "Banner-pildi URL", example = "http://example.com/banner.jpg")
    private String bannerImageUrl;
}
