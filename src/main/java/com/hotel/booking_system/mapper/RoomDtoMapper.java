package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.entity.Room;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RoomDtoMapper implements Function<Room, RoomDto> {
    public RoomDto apply(Room room) {
        return new RoomDto(room.getNumber(), room.getCapacity(), room.getPrice());
    }

    public Room fromDto(RoomDto roomDto) {
        Room room = new Room();
        room.setNumber(roomDto.getNumber());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        return room;
    }

}
