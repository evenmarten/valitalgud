package ee.bcs.valitalgud.controller.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Sündmuse täielikud üksikasjad")
public class EventDetailsResponseDto {

    @Schema(description = "Sündmuse ID", example = "1")
    private Integer eventId;

    @Schema(description = "Pealkiri", example = "Suur Tehnoloogiakonverents")
    private String title;

    @Schema(description = "Kirjeldus", example = "Aastane konverents...")
    private String description;

    @Schema(description = "Linn", example = "Tallinn")
    private String city;

    @Schema(description = "Maakond", example = "Harju maakond")
    private String county;

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

    @Schema(description = "Praegune registreerunute arv (status = LAHEB)", example = "42")
    private Integer currentParticipants;

    @Schema(description = "Oskuste-tagide nimed", example = "[\"IT\", \"Development\"]")
    private List<String> skillTags;

    @Schema(description = "Banner-pildi URL", example = "http://example.com/banner.jpg")
    private String bannerImageUrl;

    @Schema(description = "Organisaatori kasutaja ID", example = "5")
    private Integer organizerId;

    @Schema(description = "Organisaatori nimi", example = "Tech Events OÜ")
    private String organizerName;

    @Schema(description = "Organisaatori e-post", example = "organizer@example.com")
    private String organizerEmail;

    @Schema(description = "Kasutaja registreerumise staatus (null kui pole registreerunud)",
            example = "LAHEB", nullable = true,
            allowableValues = {"LAHEB", "VOIB_OLLA", "EI_LAHE"})
    private String userRegistrationStatus;
}
