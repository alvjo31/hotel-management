package com.hotel.booking_system.auth.service;

import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.model.User;
import com.hotel.booking_system.repository.UserRepository;
import com.hotel.booking_system.auth.dto.LoginRequest;
import com.hotel.booking_system.auth.dto.RegisterRequest;
import com.hotel.booking_system.auth.dto.AuthResponse;
import com.hotel.booking_system.security.jwt.JwtService;
import com.hotel.booking_system.security.user.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, User currentUser) {
        // Validation
        validateRegistrationRequest(request);

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        // Role assignment logic: Only ADMIN can assign roles
        List<String> rolesToAssign;
        if (currentUser != null && currentUser.getRoles().contains("ROLE_ADMIN")) {
            // Admin can assign any roles
            rolesToAssign = request.getRoles() != null && !request.getRoles().isEmpty()
                ? request.getRoles()
                : Collections.singletonList("ROLE_USER");
        } else {
            // Regular registration gets ROLE_USER only
            rolesToAssign = Collections.singletonList("ROLE_USER");
        }

        user.setRoles(rolesToAssign);
        userRepository.save(user);

        return AuthResponse.builder()
            .message("User registered successfully")
            .username(user.getUsername())
            .token(null)
            .roles(user.getRoles())
            .expiresIn(null)
            .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Validate login request
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            // Verify authentication was successful
            if (!authentication.isAuthenticated()) {
                throw new BadCredentialsException("Authentication failed");
            }

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            // Check if user is enabled
            if (!user.isEnabled()) {
                throw new BadRequestException("User account is disabled");
            }

            // Generate JWT token
            CustomUserDetails userDetails = new CustomUserDetails(user);
            String token = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                .message("Login successful")
                .username(user.getUsername())
                .token(token)
                .roles(user.getRoles())
                .expiresIn(jwtService.getExpirationTime())
                .build();

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (request.getUsername().length() < 3) {
            throw new BadRequestException("Username must be at least 3 characters");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        if (request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }
    }
}
