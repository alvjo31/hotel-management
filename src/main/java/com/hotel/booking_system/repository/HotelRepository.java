package com.hotel.booking_system.repository;

import com.hotel.booking_system.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    Optional<Hotel> getByHotelName(String hotelName);
    List<Hotel>  getByHotelCity(String hotelCity);



}
