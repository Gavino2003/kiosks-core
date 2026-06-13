package pt.ipvc.kiosks.bll.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.kiosks.bll.interfaces.IAuthService;
import pt.ipvc.kiosks.dal.entities.Role;
import pt.ipvc.kiosks.dal.entities.Store;
import pt.ipvc.kiosks.dal.entities.User;
import pt.ipvc.kiosks.dal.repository.RoleRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;
import pt.ipvc.kiosks.dal.repository.UserRepository;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Override
    @Transactional(readOnly = true)
    public User login(String username, String password) {
        if (username == null || password == null || username.isBlank()) return null;

        User user = userRepository.findByUsername(username.trim()).orElse(null);
        if (user == null || !user.getActive()) return null;

        if (checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    private boolean checkPassword(String rawPassword, String storedHash) {
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$")) {
            return BCrypt.checkpw(rawPassword, storedHash);
        }
        return rawPassword.equals(storedHash);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getRole() != null
                && "ADMIN".equals(user.getRole().getRoleName());
    }

    @Override
    public boolean isManager(User user) {
        return user != null && user.getRole() != null
                && ("MANAGER".equals(user.getRole().getRoleName()) || isAdmin(user));
    }

    @Override
    public boolean isOperator(User user) {
        return user != null && user.getRole() != null
                && ("OPERATOR".equals(user.getRole().getRoleName()) || isManager(user));
    }

    @Override
    @Transactional
    public User createUser(String username, String password, String email, String roleName) {
        return createUser(username, password, email, roleName, null);
    }

    @Transactional
    public User createUser(String username, String password, String email, String roleName, Long storeId) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username obrigatório");
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password deve ter pelo menos 8 caracteres");
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Email inválido");

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada: " + roleName));

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso: " + email);
        }

        Store store = null;
        if (storeId != null) {
            store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada: " + storeId));
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, hash, email, role);
        user.setStore(store);
        return userRepository.save(user);
    }
}
