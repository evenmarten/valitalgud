package ee.bcs.valitalgud.controller.myevents.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Kasutaja registreerunud sündmus")
public class MyEventResponseDto {

    @Schema(description = "Sündmuse ID", example = "1")
    private Integer eventId;

    @Schema(description = "Sündmuse pealkiri", example = "Suur Tehnoloogiakonverents")
    private String title;

    @Schema(description = "Sündmuse kuupäev", example = "2026-10-26")
    private LocalDate date;

    @Schema(description = "Asukoht (linn)", example = "Tallinn")
    private String location;

    @Schema(description = "Sündmuse kirjeldus", example = "Aastane konverents...")
    private String description;

    @Schema(description = "Kasutaja registreerumise staatus", example = "LAHEB",
            allowableValues = {"LAHEB", "VOIB_OLLA", "EI_LAHE"})
    private String userRegistrationStatus;
}
