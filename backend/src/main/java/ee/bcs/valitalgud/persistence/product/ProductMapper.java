package ee.bcs.valitalgud.persistence.product;

import ee.bcs.valitalgud.controller.product.dto.ProductDetailsResponseDto;
import ee.bcs.valitalgud.controller.product.dto.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

    @Mapping(source = "id", target = "productId")
    ProductResponseDto toProductResponseDto(Product product);

    @Mapping(source = "id", target = "productId")
    ProductDetailsResponseDto toProductDetailsResponseDto(Product product);
}