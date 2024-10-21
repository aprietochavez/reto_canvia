package com.users_roles_management.web.controller;

import com.users_roles_management.application.dto.ResponseDto;
import com.users_roles_management.application.dto.UserDto;
import com.users_roles_management.application.service.IUserService;
import com.users_roles_management.web.exception.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService iUserService;

    @PostMapping()
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            boolean isCreated = iUserService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.builder()
                            .code(HttpStatus.CREATED.value())
                            .status(isCreated)
                            .message("User created successfully")
                            .build());
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.builder()
                            .code(HttpStatus.CONFLICT.value())
                            .status(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UserDto>> findUsersAll() {
        List<UserDto> userDtos = UserDto.toDto(iUserService.findUsersAll());
        if (userDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable("id") Long id) {
        return iUserService.findUserById(id)
                .map(user -> ResponseEntity.ok(UserDto.toDto(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Boolean> updateUser(@RequestBody UserDto userDto) {
        try {
            boolean isUpdated = iUserService.updateUser(userDto);
            return ResponseEntity.ok(isUpdated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(false);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteUserById(@PathVariable("id") Long id) {
        try {
            boolean isDeleted = iUserService.deleteUserById(id);
            return ResponseEntity.ok(ResponseDto.builder()
                    .status(isDeleted)
                    .message("User deleted successfully.")
                    .build());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.builder()
                            .status(false)
                            .message("User not found: " + ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.builder()
                            .status(false)
                            .message("An error occurred while trying to delete the user: " + ex.getMessage())
                            .build());
        }
    }

}
