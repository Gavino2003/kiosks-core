package pt.ipvc.kiosks.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.kiosks.api.dto.UserDto;
import pt.ipvc.kiosks.bll.services.AuthService;
import pt.ipvc.kiosks.dal.entities.User;
import pt.ipvc.kiosks.dal.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired private AuthService     authService;
    @Autowired private UserRepository  userRepository;

    /** POST /api/auth/login  body: { username, password } */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        User user = authService.login(body.get("username"), body.get("password"));
        if (user == null) return ResponseEntity.status(401).body("Credenciais inválidas");
        UserDto dto = UserDto.from(user);
        dto.roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        return ResponseEntity.ok(dto);
    }

    /** GET /api/auth/users */
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    /** POST /api/auth/users  body: { username, password, email, roleName, storeId? } */
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

    /** PATCH /api/auth/users/{id}/active */
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
