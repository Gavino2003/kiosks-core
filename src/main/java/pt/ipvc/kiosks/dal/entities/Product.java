package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductStore> storeAssociations = new ArrayList<>();

    public Product() {}

    public Product(String productName, String description, BigDecimal price, String sku, Category category) {
        this.productName = productName;
        this.description = description;
        this.price       = price;
        this.sku         = sku;
        this.category    = category;
        this.active      = true;
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    public Long      getIdProduct()   { return idProduct; }
    public String    getProductName() { return productName; }
    public void      setProductName(String v) { this.productName = v; }
    public String    getDescription() { return description; }
    public void      setDescription(String v) { this.description = v; }
    public BigDecimal getPrice()      { return price; }
    public void      setPrice(BigDecimal v) { this.price = v; }
    public String    getImageUrl()    { return imageUrl; }
    public void      setImageUrl(String v) { this.imageUrl = v; }
    public String    getSku()         { return sku; }
    public void      setSku(String v) { this.sku = v; }
    public Boolean   getActive()      { return active; }
    public void      setActive(Boolean v) { this.active = v; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }
    public void      setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public Category  getCategory()    { return category; }
    public void      setCategory(Category v) { this.category = v; }
    public List<ProductStore> getStoreAssociations() { return storeAssociations; }

    @Override
    public String toString() { return productName + " - " + price + "€"; }
}
