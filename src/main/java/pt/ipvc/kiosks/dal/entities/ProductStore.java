package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "product_store")
public class ProductStore {

    @EmbeddedId
    private ProductStoreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProduct")
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idStore")
    @JoinColumn(name = "id_store")
    private Store store;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public ProductStore() {}

    public ProductStore(Product product, Store store, int stockQuantity) {
        this.product       = product;
        this.store         = store;
        this.stockQuantity = stockQuantity;
        this.active        = true;
        this.id            = new ProductStoreId(product.getIdProduct(), store.getIdStore());
    }

    public ProductStoreId getId()           { return id; }
    public Product getProduct()             { return product; }
    public Store   getStore()               { return store; }
    public Integer getStockQuantity()       { return stockQuantity; }
    public void    setStockQuantity(Integer q) { this.stockQuantity = q; }
    public Boolean getActive()              { return active; }
    public void    setActive(Boolean active){ this.active = active; }
}
