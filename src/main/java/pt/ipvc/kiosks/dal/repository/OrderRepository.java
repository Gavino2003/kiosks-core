package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.Order;
import pt.ipvc.kiosks.dal.entities.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByReference(String reference);

    List<Order> findByReferenceContainingIgnoreCaseOrderByCreatedAtDesc(String reference);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    List<Order> findByKioskIdKioskOrderByCreatedAtDesc(Long idKiosk);

    List<Order> findByKioskStoreIdStoreOrderByCreatedAtDesc(Long idStore);

    List<Order> findByStatusAndKioskStoreIdStoreOrderByCreatedAtDesc(OrderStatus status, Long idStore);

    List<Order> findByReferenceContainingIgnoreCaseAndKioskStoreIdStoreOrderByCreatedAtDesc(String reference, Long idStore);
}
