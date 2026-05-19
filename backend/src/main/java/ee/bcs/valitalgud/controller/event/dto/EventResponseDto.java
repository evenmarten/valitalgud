package ee.bcs.valitalgud.controller.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Sündmuse andmed (kasutusel sündmuste loendis)")
public class EventResponseDto {

    @Schema(description = "Sündmuse ID", example = "1")
    private Integer eventId;

    @Schema(description = "Sündmuse pealkiri", example = "Suur Tehnoloogiakonverents")
    private String title;

    @Schema(description = "Sündmuse kirjeldus", example = "Aastane konverents...")
    private String description;

    @Schema(description = "Linna ID", example = "1")
    private Integer cityId;

    @Schema(description = "Linn", example = "Tallinn")
    private String city;

    @Schema(description = "Aadress", example = "Näidis tänav 123")
    private String address;

    @Schema(description = "Sündmuse kuupäev", example = "2026-10-26")
    private LocalDate eventDate;

    @Schema(description = "Algusaeg", example = "09:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "Lõpuaeg", example = "17:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "Maksimaalne osalejate arv", example = "100")
    private Integer maxParticipants;

    @Schema(description = "Hetke registreerunute arv (status = LAHEB)", example = "42")
    private Integer currentParticipants;

    @Schema(description = "Oskuse-tagide ID-d", example = "[1, 4]")
    private List<Integer> skillTagIds;

    @Schema(description = "Oskuse-tagide nimed", example = "[\"IT\", \"JavaScript\"]")
    private List<String> skillTags;

    @Schema(description = "Banner-pildi URL", example = "http://example.com/banner1.jpg")
    private String bannerImageUrl;

    @Schema(description = "Organisaatori kasutaja ID", example = "5")
    private Integer organizerId;

    @Schema(description = "Organisaatori nimi", example = "Tech Events OÜ")
    private String organizerName;

    @Schema(description = "Kas sündmus on tühistatud", example = "false")
    private Boolean isCancelled;
}
