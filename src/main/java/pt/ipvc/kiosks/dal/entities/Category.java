package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    private Long idCategory;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "id_store", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    public Category() {}

    public Category(String categoryName, Integer displayOrder, Store store) {
        this.categoryName = categoryName;
        this.displayOrder = displayOrder;
        this.store = store;
        this.active = true;
    }

    public Long getIdCategory() { return idCategory; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    public List<Product> getProducts() { return products; }

    @Override
    public String toString() {
        return categoryName;
    }
}
