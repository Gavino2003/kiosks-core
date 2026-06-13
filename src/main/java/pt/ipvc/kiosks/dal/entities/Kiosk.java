package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kiosks")
public class Kiosk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kiosk")
    private Long idKiosk;

    @Column(name = "kiosk_name", nullable = false, length = 100)
    private String kioskName;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    @Column(name = "model", length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private KioskStatus status = KioskStatus.ACTIVE;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "last_connection")
    private LocalDateTime lastConnection;

    @ManyToOne
    @JoinColumn(name = "id_store", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "kiosk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "kiosk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions;

    public Kiosk() {}

    public Kiosk(String kioskName, String serialNumber, String model, Store store) {
        this.kioskName = kioskName;
        this.serialNumber = serialNumber;
        this.model = model;
        this.store = store;
        this.status = KioskStatus.ACTIVE;
    }

    public Long getIdKiosk() { return idKiosk; }
    public String getKioskName() { return kioskName; }
    public void setKioskName(String kioskName) { this.kioskName = kioskName; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public KioskStatus getStatus() { return status; }
    public void setStatus(KioskStatus status) { this.status = status; }
    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }
    public LocalDateTime getLastConnection() { return lastConnection; }
    public void setLastConnection(LocalDateTime lastConnection) { this.lastConnection = lastConnection; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    public List<Order> getOrders() { return orders; }
    public List<Session> getSessions() { return sessions; }

    @Override
    public String toString() {
        return kioskName + " [" + status + "] - " + (store != null ? store.getStoreName() : "?");
    }
}
