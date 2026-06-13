package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.UserDto;
import pt.ipvc.kiosks.bll.services.AuthService;
import pt.ipvc.kiosks.dal.entities.User;
import pt.ipvc.kiosks.dal.repository.RoleRepository;
import pt.ipvc.kiosks.dal.repository.StoreRepository;
import pt.ipvc.kiosks.dal.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired private AuthService    authService;
    @Autowired private UserRepository  userRepository;
    @Autowired private RoleRepository  roleRepository;
    @Autowired private StoreRepository storeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        User user = authService.login(body.get("username"), body.get("password"));
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");
        UserDto dto = UserDto.from(user);
        dto.roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        try {
            String username = body.get("username").toString();
            String password = body.get("password").toString();
            String email    = body.get("email").toString();
            String roleName = body.get("roleName").toString();
            Long storeId    = body.containsKey("storeId") && body.get("storeId") != null
                    ? Long.valueOf(body.get("storeId").toString()) : null;
            User user = authService.createUser(username, password, email, roleName, storeId);
            return ResponseEntity.ok(UserDto.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                         @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(u -> {
            if (body.containsKey("email"))
                u.setEmail(body.get("email").toString());
            if (body.containsKey("roleName"))
                roleRepository.findByRoleName(body.get("roleName").toString()).ifPresent(u::setRole);
            if (body.containsKey("storeId")) {
                Object raw = body.get("storeId");
                if (raw == null) {
                    u.setStore(null);
                } else {
                    storeRepository.findById(Long.valueOf(raw.toString())).ifPresent(u::setStore);
                }
            }
            return ResponseEntity.ok(UserDto.from(userRepository.save(u)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @PatchMapping("/users/{id}/active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> body) {
        return userRepository.findById(id).map(u -> {
            boolean newActive = (body != null && body.containsKey("active"))
                    ? body.get("active") : !u.getActive();
            u.setActive(newActive);
            return ResponseEntity.ok(UserDto.from(userRepository.save(u)));
        }).orElse(ResponseEntity.notFound().build());
    }
}
