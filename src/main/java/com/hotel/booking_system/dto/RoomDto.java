package com.hotel.booking_system.dto;

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
    @Column(nullable = false)
    private Integer number;
    private Integer capacity;
    private Double price;
    @Column(nullable = false)
    private Enum status;
    @Column(nullable = false)
    private int maxGuests;
    private double pricePerNight;


}
