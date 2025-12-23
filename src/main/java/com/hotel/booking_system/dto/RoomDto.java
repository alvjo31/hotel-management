package com.hotel.booking_system.dto;

import com.hotel.booking_system.enums.BookingStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDto {

    private Integer number;
    private Integer capacity;
    private Double price;
    private BookingStatus status;
    private int maxGuests;
    private double pricePerNight;

}
