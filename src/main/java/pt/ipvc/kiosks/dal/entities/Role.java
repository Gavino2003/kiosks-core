package pt.ipvc.kiosks.dal.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    public Role() {}

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public Long getIdRole() { return idRole; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<User> getUsers() { return users; }

    @Override
    public String toString() {
        return roleName;
    }
}
