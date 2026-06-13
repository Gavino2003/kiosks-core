package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.OrderLine;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}
