package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.RoomDtoMapper;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomDtoMapper roomDtoMapper;
    @Mock
    private HotelRepository hotelRepository;
    @InjectMocks
    RoomService roomService;

    private RoomDto roomDto;
    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room();
        room = new Room();
        room.setId(1);
        room.setNumber(101);
        room.setCapacity(2);
        room.setMaxGuests(2);
        room.setPricePerNight(50.0);

        roomDto = new RoomDto();
        roomDto = new RoomDto();
        roomDto.setNumber(101);
        roomDto.setCapacity(2);
        roomDto.setMaxGuests(2);
        roomDto.setPricePerNight(50.0);

    }

    @Test
    void addRoom_ShouldSaveRoom_WhenDataIsValid() {
        // GIVEN
        Integer hotelId = 1;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(roomDtoMapper.fromDto(roomDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomDtoMapper.apply(room)).thenReturn(roomDto);

        // WHEN
        RoomDto result = roomService.addRoomToHotel(roomDto, hotelId);

        // THEN
        assertNotNull(result);
        verify(roomRepository).save(room);
    }

    @Test
    void addRoom_ShouldThrowException_WhenRoomNumberIsInvalid() {
        // GIVEN
        Integer hotelId = 1;
        roomDto.setNumber(0);

        // WHEN + THEN
        assertThrows(ResourceNotFindException.class,
                () -> roomService.addRoomToHotel(roomDto ,hotelId));

        verify(roomRepository, never()).save(any());
    }

    @Test
    void getRoomsByHotelNumber_ShouldThrowBadRequest_WhenHotelNumberInvalid() {
        //GIVEN
        assertThrows(BadRequestException.class,
                () -> roomService.getRoomsByHotelNumber(0));
    }

    @Test
    void getRoomsByHotelNumber_ShouldReturnRooms() {
        Integer hotelNumber = 1;
        when(roomRepository.findByHotelNumber(hotelNumber)).thenReturn(List.of(room));
        when(roomDtoMapper.apply(room)).thenReturn(roomDto);

        List<RoomDto> result = roomService.getRoomsByHotelNumber(hotelNumber);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteRoom_ShouldThrowException_WhenRoomNotFound() {
        // GIVEN
        when(roomRepository.existsById(1)).thenReturn(false);

        // WHEN + THEN
        assertThrows(ResourceNotFindException.class,
                () -> roomService.deleteRoom(1));
    }

    @Test
    void deleteRoom_ShouldDelete_WhenRoomExists() {
        // GIVEN
        when(roomRepository.existsById(1)).thenReturn(true);

        // WHEN
        roomService.deleteRoom(1);

        // THEN
        verify(roomRepository).deleteById(1);
    }
}


