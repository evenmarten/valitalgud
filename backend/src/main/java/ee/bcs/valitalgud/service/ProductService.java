package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.controller.product.dto.ProductDetailsResponseDto;
import ee.bcs.valitalgud.controller.product.dto.ProductResponseDto;
import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.NotFoundException;
import ee.bcs.valitalgud.persistence.product.Product;
import ee.bcs.valitalgud.persistence.product.ProductMapper;
import ee.bcs.valitalgud.persistence.product.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllAvailableProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(productMapper::toProductResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponseDto getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorResponse.PRODUCT_NOT_FOUND));
        return productMapper.toProductDetailsResponseDto(product);
    }
}