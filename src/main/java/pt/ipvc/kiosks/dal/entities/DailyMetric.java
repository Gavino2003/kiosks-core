package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_metrics")
public class DailyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metric")
    private Long idMetric;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions = 0;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "total_events", nullable = false)
    private Integer totalEvents = 0;

    @Column(name = "avg_duration_sec")
    private BigDecimal avgDurationSec;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @ManyToOne
    @JoinColumn(name = "id_kiosk", nullable = false)
    private Kiosk kiosk;

    @ManyToOne
    @JoinColumn(name = "id_top_product")
    private Product topProduct;

    public DailyMetric() {}

    public Long getIdMetric() { return idMetric; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public void setReferenceDate(LocalDate referenceDate) { this.referenceDate = referenceDate; }
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Integer getTotalEvents() { return totalEvents; }
    public void setTotalEvents(Integer totalEvents) { this.totalEvents = totalEvents; }
    public BigDecimal getAvgDurationSec() { return avgDurationSec; }
    public void setAvgDurationSec(BigDecimal avgDurationSec) { this.avgDurationSec = avgDurationSec; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Kiosk getKiosk() { return kiosk; }
    public void setKiosk(Kiosk kiosk) { this.kiosk = kiosk; }
    public Product getTopProduct() { return topProduct; }
    public void setTopProduct(Product topProduct) { this.topProduct = topProduct; }
}
