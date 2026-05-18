package ee.bcs.valitalgud.controller.cart;

import ee.bcs.valitalgud.controller.cart.dto.AddCartItemDto;
import ee.bcs.valitalgud.controller.cart.dto.CartItemResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ApiError;
import ee.bcs.valitalgud.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Cart", description = "Ostukorvi haldus")
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart/items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lisa toode ostukorvi", description = "Loob ostukorvi, kui kasutajal seda pole. Sama toote uuesti lisamine suurendab kogust.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toode lisatud ostukorvi",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CartItemResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Vale kogus või puuduvad väljad (INVALID_QUANTITY / INSUFFICIENT_STOCK / MISSING_FIELDS)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Kasutaja pole sisselogitud (NOT_AUTHENTICATED)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Toodet ei leitud (PRODUCT_NOT_FOUND)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiError.class)))
    })
    public CartItemResponseDto addCartItem(@RequestBody AddCartItemDto addCartItemDto) {
        return cartService.addCartItem(addCartItemDto);
    }
}