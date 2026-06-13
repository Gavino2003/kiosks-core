package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.Store;

public class StoreDto {
    public Long   id;
    public String storeName;
    public String storeType;
    public String address;
    public String city;
    public String postalCode;
    public String phone;
    public Boolean active;

    public static StoreDto from(Store s) {
        StoreDto d = new StoreDto();
        d.id         = s.getIdStore();
        d.storeName  = s.getStoreName();
        d.storeType  = s.getStoreType();
        d.address    = s.getAddress();
        d.city       = s.getCity();
        d.postalCode = s.getPostalCode();
        d.phone      = s.getPhone();
        d.active     = s.getActive();
        return d;
    }
}
