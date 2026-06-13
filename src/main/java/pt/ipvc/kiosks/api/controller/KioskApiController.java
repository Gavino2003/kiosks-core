package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.KioskDto;
import pt.ipvc.kiosks.dal.entities.Kiosk;
import pt.ipvc.kiosks.dal.entities.KioskStatus;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.KioskRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kiosks")
public class KioskApiController {

    @Autowired private KioskRepository kioskRepository;
    @Autowired private StoreRepository storeRepository;

    @GetMapping
    public List<KioskDto> getAll(@RequestParam(required = false) Long storeId,
                                  @RequestParam(required = false) String status) {
        List<Kiosk> kiosks;
        if (storeId != null) {
            kiosks = kioskRepository.findByStoreIdStore(storeId);
        } else if (status != null) {
            kiosks = kioskRepository.findByStatus(KioskStatus.valueOf(status));
        } else {
            kiosks = kioskRepository.findAll();
        }
        return kiosks.stream().map(KioskDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KioskDto> getById(@PathVariable Long id) {
        return kioskRepository.findById(id)
                .map(k -> ResponseEntity.ok(KioskDto.from(k)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long storeId = Long.valueOf(body.get("storeId").toString());
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) return ResponseEntity.badRequest().body("Loja não encontrada");
        Kiosk k = new Kiosk(
                body.get("kioskName").toString(),
                body.getOrDefault("serialNumber", "").toString(),
                body.getOrDefault("model", "").toString(),
                store);
        return ResponseEntity.ok(KioskDto.from(kioskRepository.save(k)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KioskDto> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return kioskRepository.findById(id).map(k -> {
            if (body.containsKey("kioskName"))    k.setKioskName(body.get("kioskName").toString());
            if (body.containsKey("serialNumber")) k.setSerialNumber(body.get("serialNumber").toString());
            if (body.containsKey("model"))        k.setModel(body.get("model").toString());
            if (body.containsKey("status"))       k.setStatus(KioskStatus.valueOf(body.get("status").toString()));
            if (body.containsKey("storeId")) {
                Long storeId = Long.valueOf(body.get("storeId").toString());
                storeRepository.findById(storeId).ifPresent(k::setStore);
            }
            return ResponseEntity.ok(KioskDto.from(kioskRepository.save(k)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<KioskDto> updateStatus(@PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        String newStatus = status != null ? status
                : (body != null ? body.get("status") : null);
        if (newStatus == null) return ResponseEntity.badRequest().build();
        final String s = newStatus;
        return kioskRepository.findById(id).map(k -> {
            k.setStatus(KioskStatus.valueOf(s));
            return ResponseEntity.ok(KioskDto.from(kioskRepository.save(k)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
