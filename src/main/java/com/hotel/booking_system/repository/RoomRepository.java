package com.hotel.booking_system.repository;

import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByHotelNumber(Integer hotelNumber);

    Room findById(String roomId);
}
