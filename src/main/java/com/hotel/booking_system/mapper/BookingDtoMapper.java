package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import com.hotel.booking_system.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Builder
public class BookingDtoMapper implements Function<Booking, BookingDto> {

    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookingDtoMapper(RoomRepository roomRepository, GuestRepository guestRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @Override
    public BookingDto apply(Booking booking) {
        if (booking.getRoom() == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        return new BookingDto(
                booking.getRoom().getId(),// roomid
                booking.getGuest().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getPrice(),
                booking.getBookingStatus(),
                booking.getRoom().getHotel().getHotelName(),
                (int) booking.calculateNumberOfNights(),
                booking.getGuest().getFirstName(),
                booking.getGuest().getLastName()
        );
    }


    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setPrice(bookingDto.getPrice());
        booking.setBookingStatus(bookingDto.getBookingStatus());
        return booking;
    }
}
