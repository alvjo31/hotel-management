package com.hotel.booking_system.auth.controller;

import com.hotel.booking_system.auth.dto.LoginRequest;
import com.hotel.booking_system.auth.dto.RegisterRequest;
import com.hotel.booking_system.auth.dto.AuthResponse;
import com.hotel.booking_system.auth.service.AuthService;
import com.hotel.booking_system.model.User;
import com.hotel.booking_system.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request, Principal principal) {
        User currentUser = null;

        // Merr userin e loguar (nëse ka)
        if (principal != null) {
            currentUser = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        return authService.register(request, currentUser);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
