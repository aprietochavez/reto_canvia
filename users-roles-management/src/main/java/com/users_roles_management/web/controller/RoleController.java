package com.users_roles_management.web.controller;

import com.users_roles_management.application.dto.ResponseDto;
import com.users_roles_management.application.dto.RoleDto;
import com.users_roles_management.application.service.IRoleService;
import com.users_roles_management.web.exception.RoleAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService iRoleService;

    @PostMapping()
    public ResponseEntity<ResponseDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        try {
            boolean isCreated = iRoleService.createRole(roleDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDto.builder()
                            .code(HttpStatus.CREATED.value())
                            .status(isCreated)
                            .message("Role created successfully")
                            .build());
        } catch (RoleAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseDto.builder()
                            .code(HttpStatus.CONFLICT.value())
                            .status(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> findRolesAll() {
        List<RoleDto> roleDtos = RoleDto.toDto(iRoleService.findRolesAll());
        if (roleDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roleDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> findRoleById(@PathVariable("id") Long id) {
        return iRoleService.findRoleById(id)
                .map(role -> ResponseEntity.ok(RoleDto.toDto(role)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Boolean> updateRole(@RequestBody RoleDto roleDto) {
        try {
            boolean isUpdated = iRoleService.updateRole(roleDto);
            return ResponseEntity.ok(isUpdated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(false);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteRoleById(@PathVariable("id") Long id) {
        try {
            boolean isDeleted = iRoleService.deleteRoleById(id);
            return ResponseEntity.ok(ResponseDto.builder()
                    .status(isDeleted)
                    .message("Role deleted successfully.")
                    .build());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.builder()
                            .status(false)
                            .message("Role not found: " + ex.getMessage())
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.builder()
                            .status(false)
                            .message("An error occurred while trying to delete the role: " + ex.getMessage())
                            .build());
        }
    }

}
