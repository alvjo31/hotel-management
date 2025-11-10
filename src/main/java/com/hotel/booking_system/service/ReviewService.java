package com.hotel.booking_system.service;

import com.hotel.booking_system.mapper.ReviewDtoMapper;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.ReviewRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    private HotelRepository hotelRepository;
    private GuestService guestService;
    private RoomRepository roomRepository;
}
