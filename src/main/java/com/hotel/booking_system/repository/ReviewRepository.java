package com.hotel.booking_system.repository;

import com.hotel.booking_system.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByGuestIdAndHotelId(Integer guestId, Integer hotelId);

}
