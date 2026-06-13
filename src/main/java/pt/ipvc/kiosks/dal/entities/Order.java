package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long idOrder;

    @Column(name = "reference", nullable = false, unique = true, length = 20)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_total", nullable = false)
    private BigDecimal orderTotal;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_kiosk", nullable = false)
    private Kiosk kiosk;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;

    public Order() {}

    public Order(String reference, BigDecimal orderTotal, Kiosk kiosk) {
        this.reference = reference;
        this.orderTotal = orderTotal;
        this.kiosk = kiosk;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getIdOrder() { return idOrder; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Kiosk getKiosk() { return kiosk; }
    public void setKiosk(Kiosk kiosk) { this.kiosk = kiosk; }
    public List<OrderLine> getOrderLines() { return orderLines; }

    @Override
    public String toString() {
        return reference + " [" + (status != null ? status.name() : "?") + "] " + orderTotal + "€";
    }
}
