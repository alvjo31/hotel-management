package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoMapper implements Function<Booking , BookingDto> {

    @Override
    public BookingDto apply(Booking booking) {
        return new BookingDto(
                booking.getRoom().getId(),// roomid
                booking.getGuest().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getPrice(),
                booking.getBookingStatus(),
                booking.getRoom().getHotel().getHotelName(),
                (int)booking.calculateNumberOfNights(),
                booking.getGuest().getFirstName(),
                booking.getGuest().getLastName()
        );
    }

    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setPrice(bookingDto.getPrice());
        booking.setBookingStatus(bookingDto.getStatus());
    return booking;
    }
}
