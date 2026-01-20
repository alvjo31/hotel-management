package com.hotel.booking_system.security.config;

import com.hotel.booking_system.security.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthFilter jwtFilter, 
                         UserDetailsService userDetailsService,
                         PasswordEncoder passwordEncoder) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // Hotel endpoints - ADMIN can do everything, USER can view
                        .requestMatchers("GET", "/api/v1/hotel/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("POST", "/api/v1/hotel/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("PUT", "/api/v1/hotel/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("DELETE", "/api/v1/hotel/**").hasRole("ADMIN")

                        // Room endpoints - ADMIN and MANAGER can manage, USER can view
                        .requestMatchers("GET", "/api/v1/room/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("POST", "/api/v1/room/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("PUT", "/api/v1/room/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("DELETE", "/api/v1/room/**").hasRole("ADMIN")

                        // Guest endpoints - ADMIN and MANAGER can manage, USER can view their own
                        .requestMatchers("GET", "/api/v1/guest/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("POST", "/api/v1/guest/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("PUT", "/api/v1/guest/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("DELETE", "/api/v1/guest/**").hasRole("ADMIN")

                        // Booking endpoints - All authenticated users can create, ADMIN/MANAGER can manage all
                        .requestMatchers("GET", "/api/v1/booking/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("POST", "/api/v1/booking/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("PUT", "/api/v1/booking/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("DELETE", "/api/v1/booking/**").hasAnyRole("ADMIN", "MANAGER")

                        // Review endpoints - All authenticated users can create, only own reviews can be updated
                        .requestMatchers("GET", "/api/v1/review/**").permitAll()
                        .requestMatchers("POST", "/api/v1/review/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("PUT", "/api/v1/review/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("DELETE", "/api/v1/review/**").hasAnyRole("ADMIN", "MANAGER")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
