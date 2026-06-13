package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interaction_events")
public class InteractionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    private Long idEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "extra_data", length = 1000)
    private String extraData;

    @ManyToOne
    @JoinColumn(name = "id_session", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public InteractionEvent() {}

    public InteractionEvent(EventType eventType, Session session, Product product) {
        this.eventType = eventType;
        this.session = session;
        this.product = product;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getIdEvent() { return idEvent; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public Integer getDurationSec() { return durationSec; }
    public void setDurationSec(Integer durationSec) { this.durationSec = durationSec; }
    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
