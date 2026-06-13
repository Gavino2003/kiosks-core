package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_line")
    private Long idLine;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

    // Snapshot do nome do produto no momento da encomenda (imutável)
    @Column(name = "product_name_snap", nullable = false, length = 200)
    private String productNameSnap;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    // Nullable: produto pode ser removido sem perder histórico
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public OrderLine() {}

    public OrderLine(Integer quantity, BigDecimal unitPrice, Product product, Order order) {
        if (product == null) throw new IllegalArgumentException("Produto não pode ser null numa linha de encomenda");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.product = product;
        this.productNameSnap = product.getProductName();
        this.order = order;
    }

    public Long getIdLine() { return idLine; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    public String getProductNameSnap() { return productNameSnap; }
    public void setProductNameSnap(String productNameSnap) { this.productNameSnap = productNameSnap; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    @Override
    public String toString() {
        return quantity + "x " + productNameSnap + " @ " + unitPrice + "€ = " + lineTotal + "€";
    }
}
