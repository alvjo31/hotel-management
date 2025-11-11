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
    List<Booking> findByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Integer roomId,

            LocalDate checkInDate,
            LocalDate checkOutDate
    );
    boolean existsByGuestIdAndHotelIdAndCheckoutDateBefore(Integer guestId, Integer hotelId, Date checkoutDate);
    Optional<Booking> findTopByGuestIdAndHotelIdOrderByCheckoutDateDesc(Integer guestId, Integer hotelId);

}
