package com.users_roles_management.infrastructure.persistence.repository;

import com.users_roles_management.infrastructure.persistence.entity.User;
import com.users_roles_management.infrastructure.persistence.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {


    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.user = :user")
    void deleteAllByUser(@Param("user") User user);


}
