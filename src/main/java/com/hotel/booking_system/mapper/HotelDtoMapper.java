package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Builder
public class HotelDtoMapper implements Function<Hotel, HotelDto> {
    public HotelDto apply(Hotel hotel) {

        return new HotelDto(hotel.getHotelName(),
                hotel.getHotelAddress(),
                hotel.getHotelCity(),
                hotel.getHotelDescription(),
                hotel.getHotelPhone(),
                hotel.getHotelEmail(),
                hotel.getCreatedDate(),
                hotel.getUpdatedDate());
    }

    public Hotel fromDto(HotelDto hotelDto) {
        Hotel hotel = new Hotel();
        hotel.setHotelName(hotelDto.getHotelName());
        hotel.setHotelAddress(hotelDto.getHotelAddress());
        hotel.setHotelCity(hotelDto.getHotelCity());
        hotel.setHotelDescription(hotelDto.getHotelDescription());
        hotel.setHotelPhone(hotelDto.getHotelPhone());
        hotel.setHotelEmail(hotelDto.getHotelEmail());
        hotel.setCreatedDate(hotelDto.getCreatedDate());
        hotel.setUpdatedDate(hotelDto.getUpdatedDate());
        return hotel;
    }

}
