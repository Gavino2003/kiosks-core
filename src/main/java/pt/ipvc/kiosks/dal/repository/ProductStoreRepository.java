package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.ProductStore;
import pt.ipvc.kiosks.dal.entities.ProductStoreId;

import java.util.List;

public interface ProductStoreRepository extends JpaRepository<ProductStore, ProductStoreId> {

    List<ProductStore> findByStoreIdStoreOrderByProductProductName(Long idStore);

    List<ProductStore> findByProductIdProductOrderByStoreStoreName(Long idProduct);
}
