package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.CategoryDto;
import pt.ipvc.kiosks.dal.entities.Category;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.CategoryRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private StoreRepository    storeRepository;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(required = false) Long storeId,
                                    @RequestParam(required = false) Boolean active) {
        List<Category> cats;
        if (storeId != null && active != null && active) {
            cats = categoryRepository.findByStoreIdStoreAndActiveTrue(storeId);
        } else if (storeId != null) {
            cats = categoryRepository.findByStoreIdStore(storeId);
        } else if (active != null && active) {
            cats = categoryRepository.findByActiveTrueOrderByCategoryName();
        } else {
            cats = categoryRepository.findAll();
        }
        return cats.stream().map(CategoryDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(c -> ResponseEntity.ok(CategoryDto.from(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        Long storeId = Long.valueOf(body.get("storeId").toString());
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) return ResponseEntity.badRequest().body("Loja não encontrada");
        Integer order = body.containsKey("displayOrder") ? Integer.valueOf(body.get("displayOrder").toString()) : 0;
        Category c = new Category(body.get("categoryName").toString(), order, store);
        if (body.containsKey("description")) c.setDescription(body.get("description").toString());
        return ResponseEntity.ok(CategoryDto.from(categoryRepository.save(c)));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<CategoryDto> toggleActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return categoryRepository.findById(id).map(c -> {
            c.setActive(body.getOrDefault("active", !c.getActive()));
            return ResponseEntity.ok(CategoryDto.from(categoryRepository.save(c)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
