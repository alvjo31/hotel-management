package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.model.Hotel;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper for converting between Hotel entity and HotelDto
 */
@Component
public class HotelDtoMapper implements Function<Hotel, HotelDto> {

    @Override
    public HotelDto apply(Hotel hotel) {
        return HotelDto.builder()
                .hotelId(hotel.getId())
                .name(hotel.getHotelName())
                .address(hotel.getHotelAddress())
                .city(hotel.getHotelCity())
                .description(hotel.getHotelDescription())
                .phone(hotel.getHotelPhone() != null ? String.valueOf(hotel.getHotelPhone()) : null)
                .email(hotel.getHotelEmail())
                .createdAt(hotel.getCreatedDate() != null ? hotel.getCreatedDate().atStartOfDay() : null)
                .updatedAt(hotel.getUpdatedDate() != null ? hotel.getUpdatedDate().atStartOfDay() : null)
                .build();
    }

    public Hotel fromDto(HotelDto hotelDto) {
        Hotel hotel = new Hotel();
        if (hotelDto.getHotelId() != null) {
            hotel.setId(hotelDto.getHotelId());
        }
        hotel.setHotelName(hotelDto.getName());
        hotel.setHotelAddress(hotelDto.getAddress());
        hotel.setHotelCity(hotelDto.getCity());
        hotel.setHotelDescription(hotelDto.getDescription());

        // Convert String phone to Integer
        if (hotelDto.getPhone() != null && !hotelDto.getPhone().trim().isEmpty()) {
            try {
                // Remove non-digit characters for storage
                String digitsOnly = hotelDto.getPhone().replaceAll("[^0-9]", "");
                hotel.setHotelPhone(Integer.parseInt(digitsOnly));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid phone number format: " + hotelDto.getPhone());
            }
        }

        hotel.setHotelEmail(hotelDto.getEmail());

        // Timestamps are handled by @PrePersist and @PreUpdate in entity
        return hotel;
    }

}
