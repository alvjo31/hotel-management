package com.hotel.booking_system.auth.service;

import com.hotel.booking_system.model.User;
import com.hotel.booking_system.repository.UserRepository;
import com.hotel.booking_system.auth.dto.LoginRequest;
import com.hotel.booking_system.auth.dto.RegisterRequest;
import com.hotel.booking_system.auth.dto.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthResponse("Username already exists", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(request.getRoles() != null ? request.getRoles() : Collections.singletonList("ROLE_USER"));

        userRepository.save(user);

        return new AuthResponse("User registered successfully", user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Invalid username or password", null);
        }

        return new AuthResponse("Login successful", user.getUsername());
    }
}
