package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.Kiosk;

public class KioskDto {
    public Long   id;
    public String kioskName;
    public String serialNumber;
    public String model;
    public String status;
    public Long   storeId;
    public String storeName;

    public static KioskDto from(Kiosk k) {
        KioskDto d = new KioskDto();
        d.id           = k.getIdKiosk();
        d.kioskName    = k.getKioskName();
        d.serialNumber = k.getSerialNumber();
        d.model        = k.getModel();
        d.status       = k.getStatus() != null ? k.getStatus().name() : null;
        if (k.getStore() != null) {
            d.storeId   = k.getStore().getIdStore();
            d.storeName = k.getStore().getStoreName();
        }
        return d;
    }
}
