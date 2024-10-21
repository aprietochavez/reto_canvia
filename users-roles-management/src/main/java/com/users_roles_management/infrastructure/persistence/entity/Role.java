package com.users_roles_management.infrastructure.persistence.entity;

import com.users_roles_management.application.dto.RoleDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "tbl_roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> users;

    /**
     * Convierte un RoleDto en una entidad Role.
     *
     * @param roleDto el objeto de transferencia de datos de rol.
     * @return la entidad Role correspondiente.
     */
    public static Role toModel(RoleDto roleDto) {
        if (roleDto == null) return null;
        return Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}