package ee.bcs.valitalgud.controller.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Uue sündmuse loomise päring")
public class CreateEventDto {

    @Schema(description = "Sündmuse pealkiri", example = "Uus Sündmus")
    private String title;

    @Schema(description = "Sündmuse kirjeldus", example = "Sündmuse kirjeldus")
    private String description;

    @Schema(description = "Linna ID", example = "1")
    private Integer cityId;

    @Schema(description = "Aadress", example = "Uus tänav 1")
    private String address;

    @Schema(description = "Kuupäev (peab olema tulevikus)", example = "2026-12-01")
    private LocalDate date;

    @Schema(description = "Algusaeg", example = "10:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "Lõpuaeg", example = "12:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "Maksimaalne osalejate arv", example = "50")
    private Integer maxParticipants;

    @Schema(description = "Banner-pildi URL", example = "http://example.com/banner.jpg")
    private String bannerImageUrl;

    @Schema(description = "Oskuse-tagide ID-d", example = "[1, 2]")
    private List<Integer> skillTagIds;
}
