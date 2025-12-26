package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.mapper.RoomDtoMapper;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomDtoMapper roomDtoMapper;
    private final HotelRepository hotelRepository;


    @Autowired
    public RoomService(RoomRepository roomRepository, RoomDtoMapper roomDtoMapper, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.roomDtoMapper = roomDtoMapper;
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public RoomDto addRoomToHotel(RoomDto roomDto, Integer hotelId) {
        checkRoom(roomDto);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFindException("Hotel me ID " + hotelId + " nuk ekziston"));


        Room room = roomDtoMapper.fromDto(roomDto);
        if (room.getPrice() == null) {
            room.setPrice(0.0);
        }
        room.setHotel(hotel);
        Room saved = roomRepository.save(room);
        return roomDtoMapper.apply(saved);

    }

    // validim nese ekziston nr i dhomes
    public void checkRoom(RoomDto roomDto) {
        if (roomDto.getNumber() == null || roomDto.getNumber() <= 0) {
            throw new ResourceNotFindException("Dhoma me numer " + roomDto.getNumber() + " nuk ekziston");
        }
    }

    public List<RoomDto> getRoomsByHotelNumber(Integer hotelNumber) {

        if (hotelNumber == null || hotelNumber <= 0) {
            throw new BadRequestException("Numri i hotelit është i pavlefshëm");
        }
        return roomRepository.findByHotelNumber(hotelNumber)
                .stream()
                .map(roomDtoMapper::apply)
                .peek(this::checkRoom) // mund ta përdorësh për validim
                .toList();
    }

    @Transactional
    public RoomDto updateRoom(Integer id, RoomDto roomDto) {
        checkRoom(roomDto);

        Room updated = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFindException("Room with ID " + id + " not found."));

        if (roomDto.getNumber() != null) {
            updated.setNumber(roomDto.getNumber());
        }
        if (roomDto.getCapacity() != null) {
            updated.setCapacity(roomDto.getCapacity());
        }
        if (roomDto.getPrice() != null) {
            updated.setPrice(roomDto.getPrice());
        }
        if (roomDto.getMaxGuests() > 0) {
            updated.setMaxGuests(roomDto.getMaxGuests());
        }
        if (roomDto.getPricePerNight() > 0) {
            updated.setPricePerNight(roomDto.getPricePerNight());
        }
        updated.setBookingStatus(BookingStatus.CONFIRMED);
        return roomDtoMapper.apply(updated);

    }

    public List<RoomDto> getAllRooms(Integer hotelNumber) {
        return roomRepository.findByHotelNumber(hotelNumber)
                .stream()
                .map(roomDtoMapper)
                .toList();
    }

    @Transactional
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFindException("Room with ID " + id + " not found.");
        }
        roomRepository.deleteById(id);
    }
}

