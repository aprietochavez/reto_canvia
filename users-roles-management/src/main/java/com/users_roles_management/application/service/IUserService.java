package com.users_roles_management.application.service;

import com.users_roles_management.application.dto.UserDto;
import com.users_roles_management.infrastructure.persistence.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    boolean createUser(UserDto userDto);

    List<User> findUsersAll();

    Optional<User> findUserById(Long id);

    boolean updateUser(UserDto userDto);

    boolean deleteUserById(Long id);

}
