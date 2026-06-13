package pt.ipvc.kiosks.bll.interfaces;

import pt.ipvc.kiosks.dal.entities.User;

public interface IAuthService {
    User login(String username, String password);
    boolean isAdmin(User user);
    boolean isManager(User user);
    boolean isOperator(User user);
    User createUser(String username, String password, String email, String roleName);
}
