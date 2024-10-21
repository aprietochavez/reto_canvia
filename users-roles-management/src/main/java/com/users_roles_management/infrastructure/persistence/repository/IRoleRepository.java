package com.users_roles_management.infrastructure.persistence.repository;

import com.users_roles_management.infrastructure.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}