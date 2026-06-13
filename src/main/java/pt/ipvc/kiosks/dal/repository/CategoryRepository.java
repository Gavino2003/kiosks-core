package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStoreIdStoreAndActiveTrue(Long idStore);

    List<Category> findByActiveTrueOrderByCategoryName();
}
