package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIdCategoryAndActiveTrue(Long idCategory);

    List<Product> findByProductNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrderByProductName(String name, String sku);

    List<Product> findByProductNameContainingIgnoreCaseAndActiveTrueOrderByProductName(String name);
}
