package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByActiveTrue();

    List<Store> findByStoreTypeAndActiveTrue(String storeType);
}
