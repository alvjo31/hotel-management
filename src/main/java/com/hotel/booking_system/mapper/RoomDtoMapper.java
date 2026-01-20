package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.enums.RoomStatus;
import com.hotel.booking_system.enums.RoomType;
import com.hotel.booking_system.model.Room;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper for converting between Room entity and RoomDto
 */
@Component
public class RoomDtoMapper implements Function<Room, RoomDto> {

    @Override
    public RoomDto apply(Room room) {
        return RoomDto.builder()
                .roomId(room.getId())
                .roomNumber(room.getNumber())
                .roomType(RoomType.SINGLE) // TODO: Add roomType field to Room entity
                .maxGuests(room.getMaxGuests())
                .pricePerNight(room.getPricePerNight())
                .status(convertBookingStatusToRoomStatus(room.getBookingStatus()))
                .hotelId(room.getHotel() != null ? room.getHotel().getId() : null)
                .hotelName(room.getHotel() != null ? room.getHotel().getHotelName() : null)
                .build();
    }

    public Room fromDto(RoomDto roomDto) {
        Room room = new Room();

        if (roomDto.getRoomId() != null) {
            room.setId(roomDto.getRoomId());
        }

        room.setNumber(roomDto.getRoomNumber());
        room.setMaxGuests(roomDto.getMaxGuests());
        room.setCapacity(roomDto.getMaxGuests()); // Keep capacity in sync with maxGuests
        room.setPricePerNight(roomDto.getPricePerNight());
        room.setPrice(roomDto.getPricePerNight()); // Keep price in sync with pricePerNight

        if (roomDto.getStatus() != null) {
            room.setBookingStatus(convertRoomStatusToBookingStatus(roomDto.getStatus()));
        }

        return room;
    }

    /**
     * Convert BookingStatus to RoomStatus (temporary until Room entity is refactored)
     */
    private RoomStatus convertBookingStatusToRoomStatus(com.hotel.booking_system.enums.BookingStatus bookingStatus) {
        if (bookingStatus == null) {
            return RoomStatus.AVAILABLE;
        }

        return switch (bookingStatus) {
            case CONFIRMED -> RoomStatus.OCCUPIED;
            case CANCELLED -> RoomStatus.AVAILABLE;
            case PENDING -> RoomStatus.RESERVED;
            default -> RoomStatus.AVAILABLE;
        };
    }

    /**
     * Convert RoomStatus to BookingStatus (temporary until Room entity is refactored)
     */
    private com.hotel.booking_system.enums.BookingStatus convertRoomStatusToBookingStatus(RoomStatus roomStatus) {
        if (roomStatus == null) {
            return com.hotel.booking_system.enums.BookingStatus.PENDING;
        }

        return switch (roomStatus) {
            case OCCUPIED -> com.hotel.booking_system.enums.BookingStatus.CONFIRMED;
            case RESERVED -> com.hotel.booking_system.enums.BookingStatus.PENDING;
            case AVAILABLE, MAINTAINANCE -> com.hotel.booking_system.enums.BookingStatus.CANCELLED;
        };
    }
}
