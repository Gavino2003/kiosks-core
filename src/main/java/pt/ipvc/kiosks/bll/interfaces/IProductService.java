package pt.ipvc.kiosks.bll.interfaces;

import pt.ipvc.kiosks.dal.entities.Product;
import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    List<Product> getAllProductsByStore(Long idStore);
    List<Product> getProductsByCategory(Long idCategory);
    List<Product> searchProducts(String name);
    Product getProductById(Long id);
    Product createProduct(String name, String description, BigDecimal price, String sku, Long idCategory, Long idStore);
    Product updateProduct(Long id, String name, String description, BigDecimal price);
    void deactivateProduct(Long id);
}
