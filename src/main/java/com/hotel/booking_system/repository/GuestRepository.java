package com.hotel.booking_system.repository;

import com.hotel.booking_system.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {

    boolean existsByEmail(String email);

}
