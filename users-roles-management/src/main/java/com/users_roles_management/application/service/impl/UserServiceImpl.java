package com.users_roles_management.application.service.impl;

import com.users_roles_management.application.dto.UserDto;
import com.users_roles_management.infrastructure.persistence.repository.IUserRoleRepository;
import com.users_roles_management.application.service.IUserService;
import com.users_roles_management.infrastructure.persistence.entity.Role;
import com.users_roles_management.infrastructure.persistence.entity.User;
import com.users_roles_management.infrastructure.persistence.entity.UserRole;
import com.users_roles_management.infrastructure.persistence.repository.IRoleRepository;
import com.users_roles_management.infrastructure.persistence.repository.IUserRepository;
import com.users_roles_management.web.exception.RoleNotFoundException;
import com.users_roles_management.web.exception.UserAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IUserRoleRepository iUserRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public boolean createUser(UserDto userDto) {
        if (iUserRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with username: " + userDto.getUsername());
        }
        if (iUserRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with email: " + userDto.getEmail());
        }
        User user = iUserRepository.save(User.toModel(userDto));
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            saveUserRole(userDto, user);
        } else {
            throw new RoleNotFoundException("At least one role must be assigned to the user.");
        }
        return true;
    }

    public List<User> findUsersAll() {
        return iUserRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return iUserRepository.findById(id);
    }

    @Override
    @Transactional()
    public boolean updateUser(UserDto userDto) {
        User user = iUserRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userDto.getId()));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        iUserRepository.save(user);
        iUserRoleRepository.deleteAllByUser(user);
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            throw new RoleNotFoundException("At least one role must be assigned to the user.");
        } else {
            saveUserRole(userDto, user);
        }
        return true;
    }

    private void saveUserRole(UserDto userDto, User user) {
        for (String roleName : userDto.getRoles()) {
            Role role = iRoleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            iUserRoleRepository.save(userRole);
        }
    }


    @Override
    public boolean deleteUserById(Long id) {
        if (iUserRepository.existsById(id)) {
            iUserRepository.deleteById(id);
            return true;
        } else {
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
    }

}
