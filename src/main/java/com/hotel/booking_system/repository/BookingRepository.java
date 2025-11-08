package com.hotel.booking_system.repository;

import com.hotel.booking_system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Integer roomId,

            LocalDate checkInDate,
            LocalDate checkOutDate
    );

}
