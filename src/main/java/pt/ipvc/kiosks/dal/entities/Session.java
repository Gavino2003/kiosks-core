package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long idSession;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @ManyToOne
    @JoinColumn(name = "id_kiosk", nullable = false)
    private Kiosk kiosk;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<InteractionEvent> events;

    public Session() {}

    public Session(Kiosk kiosk) {
        this.kiosk = kiosk;
        this.startedAt = LocalDateTime.now();
    }

    public Long getIdSession() { return idSession; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public Integer getDurationSec() { return durationSec; }
    public void setDurationSec(Integer durationSec) { this.durationSec = durationSec; }
    public Kiosk getKiosk() { return kiosk; }
    public void setKiosk(Kiosk kiosk) { this.kiosk = kiosk; }
    public List<InteractionEvent> getEvents() { return events; }
}
