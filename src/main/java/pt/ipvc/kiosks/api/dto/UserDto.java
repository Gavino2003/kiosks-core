package pt.ipvc.kiosks.api.dto;

import pt.ipvc.kiosks.dal.entities.User;

public class UserDto {
    public Long    id;
    public String  username;
    public String  email;
    public Boolean active;
    public String  roleName;
    public Long    storeId;
    public String  storeName;

    public static UserDto from(User u) {
        UserDto d = new UserDto();
        d.id       = u.getIdUser();
        d.username = u.getUsername();
        d.email    = u.getEmail();
        d.active   = u.getActive();
        if (u.getRole() != null)  d.roleName  = u.getRole().getRoleName();
        if (u.getStore() != null) { d.storeId = u.getStore().getIdStore(); d.storeName = u.getStore().getStoreName(); }
        return d;
    }
}
