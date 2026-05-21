package ee.bcs.valitalgud.controller.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Kontaktivormi päring")
public class ContactRequestDto {

    @Schema(description = "Saatja nimi või ettevõte", example = "Mari Maasikas")
    private String nameOrCompany;

    @Schema(description = "Saatja e-mail (kasutatakse vastamiseks)", example = "mari@näide.ee")
    private String email;

    @Schema(description = "Sõnumi sisu", example = "Tere! Sooviksin teiega koostööd teha.")
    private String message;
}