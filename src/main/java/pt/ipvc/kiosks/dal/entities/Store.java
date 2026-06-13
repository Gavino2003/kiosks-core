package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_store")
    private Long idStore;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @Column(name = "store_type", nullable = false, length = 50)
    private String storeType;   // EYEWEAR | MAKEUP | JEWELLERY

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Kiosk> kiosks;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<ProductStore> productAssociations;

    public Store() {}

    public Store(String storeName, String storeType, String address, String city, String postalCode) {
        this.storeName = storeName;
        this.storeType = storeType;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getIdStore() { return idStore; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreType() { return storeType; }
    public void setStoreType(String storeType) { this.storeType = storeType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Kiosk> getKiosks() { return kiosks; }
    public List<Category> getCategories() { return categories; }
    public List<ProductStore> getProductAssociations() { return productAssociations; }

    @Override
    public String toString() {
        return storeName + " [" + storeType + "] - " + city;
    }
}
