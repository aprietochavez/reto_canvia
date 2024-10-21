package com.users_roles_management.application.service.impl;

import com.users_roles_management.application.dto.RoleDto;
import com.users_roles_management.application.service.IRoleService;
import com.users_roles_management.infrastructure.persistence.entity.Role;
import com.users_roles_management.infrastructure.persistence.repository.IRoleRepository;
import com.users_roles_management.web.exception.RoleAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository iRoleRepository;

    @Override
    public boolean createRole(RoleDto roleDto) {
        if (iRoleRepository.findByName(roleDto.getName()).isPresent()) {
            throw new RoleAlreadyExistsException("Role already exists with name: " + roleDto.getName());
        }
        iRoleRepository.save(Role.toModel(roleDto));
        return true;
    }

    public List<Role> findRolesAll() {
        return iRoleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return iRoleRepository.findById(id);
    }

    @Override
    public boolean updateRole(RoleDto roleDto) {
        Optional<Role> existingRole = iRoleRepository.findById(roleDto.getId());
        if (existingRole.isPresent()) {
            iRoleRepository.save(Role.toModel(roleDto));
            return true;
        } else {
            throw new EntityNotFoundException("Role not found with id: " + roleDto.getId());
        }
    }

    @Override
    public boolean deleteRoleById(Long id) {
        if (iRoleRepository.existsById(id)) {
            iRoleRepository.deleteById(id);
            return true;
        } else {
            throw new EntityNotFoundException("Role with ID " + id + " not found.");
        }
    }

}