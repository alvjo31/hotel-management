package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BookingDtoMapper implements Function<Booking , BookingDto> {
    public BookingDto apply(Booking booking) {
        return new BookingDto(booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getPrice(),booking.getStatus());
    }

    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setPrice(bookingDto.getPrice());
        booking.setStatus(bookingDto.getStatus());
    return booking;
    }
}
