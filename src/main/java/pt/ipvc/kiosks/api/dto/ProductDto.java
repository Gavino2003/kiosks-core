package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.Product;
import pt.ipvc.kiosks.dal.entities.ProductStore;

import java.math.BigDecimal;
import java.util.List;

public class ProductDto {
    public Long       id;
    public String     productName;
    public String     description;
    public BigDecimal price;
    public String     sku;
    public String     imageUrl;
    public Boolean    active;
    public Long       categoryId;
    public String     categoryName;
    public Integer    stockQuantity;   // stock na loja pedida (opcional)
    public List<Long> storeIds;

    public static ProductDto from(Product p) {
        ProductDto d = new ProductDto();
        d.id           = p.getIdProduct();
        d.productName  = p.getProductName();
        d.description  = p.getDescription();
        d.price        = p.getPrice();
        d.sku          = p.getSku();
        d.imageUrl     = p.getImageUrl();
        d.active       = p.getActive();
        if (p.getCategory() != null) {
            d.categoryId   = p.getCategory().getIdCategory();
            d.categoryName = p.getCategory().getCategoryName();
        }
        d.storeIds = p.getStoreAssociations().stream()
                .map(ps -> ps.getStore().getIdStore())
                .toList();
        return d;
    }

    public static ProductDto from(ProductStore ps) {
        ProductDto d = from(ps.getProduct());
        d.stockQuantity = ps.getStockQuantity();
        return d;
    }
}
