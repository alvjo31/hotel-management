package com.hotel.booking_system.dto;

import com.hotel.booking_system.enums.RoomStatus;
import com.hotel.booking_system.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Room entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDto {

    private Integer roomId;

    @NotNull(message = "Room number is required")
    @Positive(message = "Room number must be positive")
    private Integer roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Maximum guests is required")
    @Min(value = 1, message = "Maximum guests must be at least 1")
    @Max(value = 10, message = "Maximum guests cannot exceed 10")
    private Integer maxGuests;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price is too high")
    private Double pricePerNight;

    @NotNull(message = "Room status is required")
    private RoomStatus status;

    private Integer hotelId;
    private String hotelName;
}
