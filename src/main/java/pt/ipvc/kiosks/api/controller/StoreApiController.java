package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.StoreDto;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.StoreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
public class StoreApiController {

    @Autowired private StoreRepository storeRepository;

    @GetMapping
    public List<StoreDto> getAll(@RequestParam(required = false) Boolean active) {
        List<Store> stores = (active != null && active)
                ? storeRepository.findByActiveTrue()
                : storeRepository.findAll();
        return stores.stream().map(StoreDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getById(@PathVariable Long id) {
        return storeRepository.findById(id)
                .map(s -> ResponseEntity.ok(StoreDto.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StoreDto create(@RequestBody Map<String, String> body) {
        Store s = new Store(
                body.get("storeName"), body.get("storeType"),
                body.get("address"),   body.get("city"), body.get("postalCode"));
        if (body.containsKey("phone")) s.setPhone(body.get("phone"));
        return StoreDto.from(storeRepository.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return storeRepository.findById(id).map(s -> {
            if (body.containsKey("storeName"))  s.setStoreName(body.get("storeName"));
            if (body.containsKey("storeType"))  s.setStoreType(body.get("storeType"));
            if (body.containsKey("address"))    s.setAddress(body.get("address"));
            if (body.containsKey("city"))       s.setCity(body.get("city"));
            if (body.containsKey("postalCode")) s.setPostalCode(body.get("postalCode"));
            if (body.containsKey("phone"))      s.setPhone(body.get("phone"));
            return ResponseEntity.ok(StoreDto.from(storeRepository.save(s)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<StoreDto> toggleActive(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> body) {
        return storeRepository.findById(id).map(s -> {
            boolean newActive = (body != null && body.containsKey("active"))
                    ? body.get("active") : !s.getActive();
            s.setActive(newActive);
            return ResponseEntity.ok(StoreDto.from(storeRepository.save(s)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
