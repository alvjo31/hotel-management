package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.entity.Hotel;
import com.hotel.booking_system.mapper.HotelDtoMapper;
import com.hotel.booking_system.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;

    private final HotelDtoMapper hotelDtoMapper;

    @Autowired
    public HotelService(HotelRepository hotelRepository, HotelDtoMapper hotelDtoMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelDtoMapper = hotelDtoMapper;
    }


    public HotelDto getHotelById(int id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotelDtoMapper.apply(hotel.get());
    }

    public HotelDto getHotelByName(String hotelName) {
        Optional<Hotel> hotelByName = hotelRepository.getHotelByName(hotelName);

        // if (hotelByName.isPresent()) {
        //   return hotelDtoMapper.apply(hotelByName.get());
        // }else
        //   throw new RuntimeException("Hotel me emrin '" + hotelName + "' nuk u gjet.");

        return hotelRepository.getHotelByName(hotelName)
                .map(hotelDtoMapper)
                .orElseThrow(() -> new RuntimeException(
                        "Hotel me emrin '" + hotelName + "' nuk u gjet."
                ));
    }
}


