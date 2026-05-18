package ee.bcs.valitalgud.persistence.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByStockQuantityGreaterThan(int quantity);
}