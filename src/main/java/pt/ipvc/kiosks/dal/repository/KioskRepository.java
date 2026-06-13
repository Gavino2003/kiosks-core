package pt.ipvc.kiosks.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.kiosks.dal.entities.Kiosk;
import pt.ipvc.kiosks.dal.entities.KioskStatus;

import java.util.List;

public interface KioskRepository extends JpaRepository<Kiosk, Long> {

    List<Kiosk> findByStoreIdStore(Long idStore);

    List<Kiosk> findByStatus(KioskStatus status);
}
