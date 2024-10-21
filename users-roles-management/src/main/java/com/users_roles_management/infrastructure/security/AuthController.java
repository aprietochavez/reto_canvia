package com.users_roles_management.infrastructure.security;

import com.users_roles_management.infrastructure.persistence.entity.User;
import com.users_roles_management.infrastructure.persistence.repository.IUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final IUserRepository iUserRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, IUserRepository iUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.iUserRepository = iUserRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Obtener el usuario autenticado
            User user = iUserRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Obtener los roles del usuario
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getRole().getName())
                    .collect(Collectors.toList());

            // Generar el token con los roles
            String jwt = jwtTokenService.generateToken(authentication.getName(), roles);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
