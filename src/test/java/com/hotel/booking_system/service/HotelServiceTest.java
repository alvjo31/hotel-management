package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.mapper.HotelDtoMapper;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HotelDtoMapper hotelDtoMapper;
    @InjectMocks
    private HotelService hotelService;

    @Test
    void getHotelById_ShouldReturnHotelDto_WhenHotelExists() {
        //given
        Hotel hotel = new Hotel();
        HotelDto hotelDto = new HotelDto();
        when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel));
        when(hotelDtoMapper.apply(hotel)).thenReturn(hotelDto);
    }

}
