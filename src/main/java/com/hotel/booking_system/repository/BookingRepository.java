package com.hotel.booking_system.repository;

import com.hotel.booking_system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(
            Integer roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    boolean existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
            Integer guestId,
            Integer hotelId,
            LocalDate checkOutDate
    );

    Optional<Booking> findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(
            Integer guestId,
            Integer hotelId
    );
}

