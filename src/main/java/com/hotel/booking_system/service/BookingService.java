package com.hotel.booking_system.service;

import com.hotel.booking_system.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    //first comment
    @Autowired
    private BookingRepository bookingRepository;
}
