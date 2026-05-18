package ee.bcs.valitalgud.controller.product;

import ee.bcs.valitalgud.controller.product.dto.ProductDetailsResponseDto;
import ee.bcs.valitalgud.controller.product.dto.ProductResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Products", description = "E-poe toodete haldus")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta kõik saadaolevad tooted", description = "Tagastab tooted, kus stock_quantity > 0.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tooted edukalt tagastatud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))))
    })
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllAvailableProducts();
    }

    @GetMapping(value = "/products/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tagasta üksiku toote detailid", description = "Tagastab toote täisandmed külgpaneeli jaoks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toode edukalt leitud",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDetailsResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Toodet ei leitud (PRODUCT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public ProductDetailsResponseDto getProductById(@PathVariable Integer productId) {
        return productService.getProductById(productId);
    }
}