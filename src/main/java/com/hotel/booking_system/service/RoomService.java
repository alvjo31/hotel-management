package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.mapper.RoomDtoMapper;
import com.hotel.booking_system.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoomService {

    private RoomRepository roomRepository;
    private RoomDtoMapper roomDtoMapper;

    @Autowired
    public RoomService(RoomRepository roomRepository, RoomDtoMapper roomDtoMapper) {
        this.roomRepository = roomRepository;
        this.roomDtoMapper = roomDtoMapper;
    }


    public RoomDto addRoom(RoomDto roomDto) {
        checkRoom(roomDto);
        Room room = new Room();
        Room saved = roomRepository.save(room);
        return roomDtoMapper.apply(room);

    }

    // validim nese ekziston nr i dhomes
    public void checkRoom(RoomDto roomDto) {
        if (roomDto.getNumber() == null || roomDto.getNumber() <= 0) {
            throw new RuntimeException("Dhoma me numer " + roomDto.getNumber());
        }
    }

    public List<RoomDto> getRoomsByHotelNumber(Integer hotelNumber) {

        if (hotelNumber == null || hotelNumber <= 0) {
            throw new RuntimeException("Numri i hotelit është i pavlefshëm");
        }
        return roomRepository.findByHotelNumber(hotelNumber)
                .stream()
                .map(roomDtoMapper::apply)
                .peek(this::checkRoom) // mund ta përdorësh për validim
                .toList();
    }

    public RoomDto updateRoom(RoomDto roomDto) {
        checkRoom(roomDto);

        Room updated = roomDtoMapper.fromDto(roomDto);
        Room saved = roomRepository.save(updated);
        return roomDtoMapper.apply(saved);

    }

    public List<RoomDto> getAllRooms(Integer hotelNumber) {
        return roomRepository.findByHotelNumber(hotelNumber)
                .stream()
                .map(roomDtoMapper)
                .toList();
    }
}

