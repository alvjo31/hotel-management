package com.hotel.booking_system.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDto {
    private String hotelName;
    private String hotelAddress;
    private String hotelCity;
    private String hotelDescription;
    private Integer hotelPhone;
    private String hotelEmail;
    private LocalDate createdDate;
    private LocalDate updatedDate;
}
