package com.hotel.booking_system.auth.controller;

import com.hotel.booking_system.auth.dto.LoginRequest;
import com.hotel.booking_system.auth.dto.RegisterRequest;
import com.hotel.booking_system.auth.dto.AuthResponse;
import com.hotel.booking_system.auth.service.AuthService;
import com.hotel.booking_system.model.User;
import com.hotel.booking_system.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Public registration endpoint - creates USER role by default
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Admin-only registration endpoint - can assign any roles
     * Requires ROLE_ADMIN
     */
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerByAdmin(
            @Valid @RequestBody RegisterRequest request,
            Authentication authentication) {

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElse(null);

        AuthResponse response = authService.register(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login endpoint - returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user info - requires authentication
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponse response = AuthResponse.builder()
                .message("Current user details")
                .username(user.getUsername())
                .token(null)
                .roles(user.getRoles())
                .expiresIn(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
