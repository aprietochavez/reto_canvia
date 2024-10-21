package com.users_roles_management.application.service;

import com.users_roles_management.application.dto.RoleDto;
import com.users_roles_management.infrastructure.persistence.entity.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    boolean createRole(RoleDto roleDto);

    List<Role> findRolesAll();

    Optional<Role> findRoleById(Long id);

    boolean updateRole(RoleDto roleDto);

    boolean deleteRoleById(Long id);

}