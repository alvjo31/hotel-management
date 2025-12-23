package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
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
                booking.getCheckoutDate(),
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
        booking.setCheckoutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setBookingStatus(bookingDto.getBookingStatus());

        // Vendos room dhe guest
        booking.setRoom(roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFindException("Room not found")));
        booking.setGuest(guestRepository.findById(bookingDto.getGuestId())
                .orElseThrow(() -> new ResourceNotFindException("Guest not found")));
        return booking;
    }
}
