package com.hotel.booking_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.booking_system.enums.BookingStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request DTO for creating/updating a booking
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be positive")
    private Integer roomId;

    @NotNull(message = "Guest ID is required")
    @Positive(message = "Guest ID must be positive")
    private Integer guestId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 10, message = "Maximum 10 guests allowed")
    private Integer numberOfGuests;

    // Response fields (populated by server)
    private Integer bookingId;
    private Double totalPrice;
    private BookingStatus status;
    private String hotelName;
    private Integer numberOfNights;
    private String guestFirstName;
    private String guestLastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
