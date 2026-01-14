package com.hotel.booking_system.auth.dto;

public class AuthResponse {
    private String message;
    private String username;

    public AuthResponse() {}

    public AuthResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
