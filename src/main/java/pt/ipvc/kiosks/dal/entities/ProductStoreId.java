package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductStoreId implements Serializable {

    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "id_store")
    private Long idStore;

    public ProductStoreId() {}
    public ProductStoreId(Long idProduct, Long idStore) {
        this.idProduct = idProduct;
        this.idStore   = idStore;
    }

    public Long getIdProduct() { return idProduct; }
    public Long getIdStore()   { return idStore; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductStoreId)) return false;
        ProductStoreId that = (ProductStoreId) o;
        return Objects.equals(idProduct, that.idProduct) && Objects.equals(idStore, that.idStore);
    }
    @Override public int hashCode() { return Objects.hash(idProduct, idStore); }
}
