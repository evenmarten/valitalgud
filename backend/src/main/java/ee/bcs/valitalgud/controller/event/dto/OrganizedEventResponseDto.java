package ee.bcs.valitalgud.controller.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Korraldatud sündmuse rida MyOrganizedEventsView tabelis")
public class OrganizedEventResponseDto {

    @Schema(description = "Sündmuse ID", example = "1")
    private Integer eventId;

    @Schema(description = "Sündmuse pealkiri", example = "Suur Tehnoloogiakonverents")
    private String title;

    @Schema(description = "Sündmuse kuupäev", example = "2026-10-26")
    private LocalDate date;

    @Schema(description = "Linn", example = "Tallinn")
    private String city;

    @Schema(description = "Maakond", example = "Harju maakond")
    private String county;

    @Schema(description = "Sündmuse staatus", example = "Aktiivne",
            allowableValues = {"Aktiivne", "Lõppenud", "Tühistatud"})
    private String status;

    @Schema(description = "Hetkel registreerunute arv (status = LAHEB)", example = "25")
    private Integer currentParticipants;

    @Schema(description = "Maksimaalne osalejate arv (võib olla null)", example = "100", nullable = true)
    private Integer maxParticipants;
}
