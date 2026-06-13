package pt.ipvc.kiosks.bll.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.kiosks.bll.interfaces.IProductService;
import pt.ipvc.kiosks.dal.entities.*;
import pt.ipvc.kiosks.dal.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired private ProductRepository      productRepository;
    @Autowired private ProductStoreRepository productStoreRepository;
    @Autowired private CategoryRepository     categoryRepository;
    @Autowired private StoreRepository        storeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProductsByStore(Long idStore) {
        if (idStore == null) return productRepository.findAll();
        return productStoreRepository.findByStoreIdStoreOrderByProductProductName(idStore)
                .stream().map(ProductStore::getProduct).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long idCategory) {
        return productRepository.findByCategoryIdCategoryAndActiveTrue(idCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String term) {
        if (term == null || term.isBlank()) return List.of();
        return productRepository.findByProductNameContainingIgnoreCaseOrSkuContainingIgnoreCaseOrderByProductName(term.trim(), term.trim());
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByStore(String term, Long idStore) {
        if (term == null || term.isBlank()) return getAllProductsByStore(idStore);

        String t = term.trim().toLowerCase();
        List<Product> base = getAllProductsByStore(idStore);
        return base.stream()
                .filter(p -> p.getProductName().toLowerCase().contains(t)
                        || (p.getSku() != null && p.getSku().toLowerCase().contains(t)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Product createProduct(String name, String description, BigDecimal price,
                                  String sku, Long idCategory, Long idStore) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome do produto obrigatório");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço inválido");

        Category category = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Product product = new Product(name.trim(), description, price, sku, category);
        product = productRepository.save(product);

        if (idStore != null) {
            Store store = storeRepository.findById(idStore)
                    .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));
            ProductStore ps = new ProductStore(product, store, 0);
            productStoreRepository.save(ps);
        }

        return product;
    }

    /** Cria produto e associa a várias lojas de uma só vez */
    @Transactional
    public Product createProduct(String name, String description, BigDecimal price,
                                  String sku, Long idCategory, List<Long> storeIds) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome do produto obrigatório");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço inválido");
        if (storeIds == null || storeIds.isEmpty()) throw new IllegalArgumentException("Selecione pelo menos uma loja");

        Category category = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Product product = new Product(name.trim(), description, price, sku, category);
        product = productRepository.save(product);

        for (Long sid : storeIds) {
            Store store = storeRepository.findById(sid)
                    .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada: " + sid));
            productStoreRepository.save(new ProductStore(product, store, 0));
        }

        return product;
    }

    /** Actualiza as lojas associadas a um produto (adiciona novas, remove as ausentes) */
    @Transactional
    public void updateStoreAssociations(Product product, List<Store> newStores) {
        List<ProductStore> existing = productStoreRepository.findByProductIdProductOrderByStoreStoreName(product.getIdProduct());

        // remover associações que já não estão na lista
        for (ProductStore ps : existing) {
            boolean keep = newStores.stream().anyMatch(s -> s.getIdStore().equals(ps.getStore().getIdStore()));
            if (!keep) productStoreRepository.delete(ps);
        }

        // adicionar novas
        for (Store store : newStores) {
            ProductStoreId pid = new ProductStoreId(product.getIdProduct(), store.getIdStore());
            if (!productStoreRepository.existsById(pid)) {
                productStoreRepository.save(new ProductStore(product, store, 0));
            }
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, String name, String description, BigDecimal price) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        if (name != null && !name.isBlank()) product.setProductName(name.trim());
        if (description != null) product.setDescription(description);
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) product.setPrice(price);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductStore> getStoreAssociations(Long idProduct) {
        return productStoreRepository.findByProductIdProductOrderByStoreStoreName(idProduct);
    }
}
