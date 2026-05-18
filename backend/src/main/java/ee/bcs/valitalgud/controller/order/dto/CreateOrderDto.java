package ee.bcs.valitalgud.controller.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Uue tellimuse loomise andmed")
public class CreateOrderDto {

    @Schema(description = "Kasutaja ID (valikuline, null külalise puhul)", example = "1")
    private Integer userId;

    @Schema(description = "Eesnimi", example = "Mari")
    private String firstName;

    @Schema(description = "Perekonnanimi", example = "Maasikas")
    private String lastName;

    @Schema(description = "Ettevõtte nimi (valikuline)", example = "Ettevõte OÜ")
    private String companyName;

    @Schema(description = "Riik", example = "Eesti")
    private String country;

    @Schema(description = "Tänav ja majanumber", example = "Pargi tee 5")
    private String street;

    @Schema(description = "Postiindeks", example = "10115")
    private String postalCode;

    @Schema(description = "Linn", example = "Tallinn")
    private String city;

    @Schema(description = "Telefoninumber", example = "+372 555 1234")
    private String phone;

    @Schema(description = "E-post", example = "mari@example.com")
    private String email;

    @Schema(description = "Tellimuse read")
    private List<OrderItemDto> items;
}