package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.ProductDto;
import pt.ipvc.kiosks.bll.services.ProductService;
import pt.ipvc.kiosks.dal.entities.Product;
import pt.ipvc.kiosks.dal.entities.ProductStore;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.repository.ProductRepository;
import pt.ipvc.kiosks.dal.repository.ProductStoreRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired private ProductService         productService;
    @Autowired private ProductRepository      productRepository;
    @Autowired private ProductStoreRepository productStoreRepository;
    @Autowired private StoreRepository        storeRepository;

    /** GET /api/products?storeId=1&categoryId=2&q=ray */
    @GetMapping
    public List<ProductDto> getAll(@RequestParam(required = false) Long storeId,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false) String q) {
        if (storeId != null) {
            List<ProductStore> ps = productStoreRepository.findByStoreIdStoreOrderByProductProductName(storeId)
                    .stream()
                    .filter(p -> p.getProduct().getActive())
                    .filter(p -> categoryId == null || (p.getProduct().getCategory() != null
                            && p.getProduct().getCategory().getIdCategory().equals(categoryId)))
                    .filter(p -> q == null || p.getProduct().getProductName().toLowerCase().contains(q.toLowerCase())
                            || (p.getProduct().getSku() != null && p.getProduct().getSku().toLowerCase().contains(q.toLowerCase())))
                    .toList();
            return ps.stream().map(ProductDto::from).toList();
        }
        return productService.getAllProductsByStore(null).stream().map(ProductDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        Product p = productService.getProductById(id);
        return p != null ? ResponseEntity.ok(ProductDto.from(p)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String name   = body.get("productName").toString();
            String desc   = body.getOrDefault("description", "").toString();
            BigDecimal price = new BigDecimal(body.get("price").toString());
            String sku    = body.containsKey("sku") ? body.get("sku").toString() : null;
            Long catId    = Long.valueOf(body.get("categoryId").toString());

            @SuppressWarnings("unchecked")
            List<Integer> rawIds = (List<Integer>) body.get("storeIds");
            List<Long> storeIds  = rawIds.stream().map(Long::valueOf).toList();

            Product p = productService.createProduct(name, desc.isEmpty() ? null : desc, price, sku, catId, storeIds);

            if (body.containsKey("imageUrl") && body.get("imageUrl") != null) {
                p.setImageUrl(body.get("imageUrl").toString());
                productRepository.save(p);
            }
            return ResponseEntity.ok(ProductDto.from(p));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Product p = productRepository.findById(id).orElse(null);
            if (p == null) return ResponseEntity.notFound().build();

            if (body.containsKey("productName")) p.setProductName(body.get("productName").toString());
            if (body.containsKey("description"))  p.setDescription(body.get("description").toString());
            if (body.containsKey("price"))         p.setPrice(new BigDecimal(body.get("price").toString()));
            if (body.containsKey("sku"))           p.setSku(body.get("sku").toString());
            if (body.containsKey("imageUrl"))      p.setImageUrl(body.get("imageUrl") != null ? body.get("imageUrl").toString() : null);
            if (body.containsKey("active"))        p.setActive(Boolean.valueOf(body.get("active").toString()));

            productRepository.save(p);

            if (body.containsKey("storeIds")) {
                @SuppressWarnings("unchecked")
                List<Integer> rawIds = (List<Integer>) body.get("storeIds");
                List<Store> stores = rawIds.stream()
                        .map(sid -> storeRepository.findById(Long.valueOf(sid)).orElse(null))
                        .filter(s -> s != null)
                        .toList();
                productService.updateStoreAssociations(p, stores);
            }

            return ResponseEntity.ok(ProductDto.from(p));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        try {
            productService.deactivateProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
