package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.repository.GuestRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper for converting between Booking entity and BookingDto
 */
@Component
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
            throw new IllegalArgumentException("Room cannot be null in booking");
        }
        if (booking.getGuest() == null) {
            throw new IllegalArgumentException("Guest cannot be null in booking");
        }

        return BookingDto.builder()
                .bookingId(booking.getId())
                .roomId(booking.getRoom().getId())
                .guestId(booking.getGuest().getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckoutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalPrice(booking.getPrice())
                .status(booking.getBookingStatus())
                .hotelName(booking.getRoom().getHotel() != null ?
                    booking.getRoom().getHotel().getHotelName() : null)
                .numberOfNights((int) booking.calculateNumberOfNights())
                .guestFirstName(booking.getGuest().getFirstName())
                .guestLastName(booking.getGuest().getLastName())
                .createdAt(booking.getCreatedDate())
                .updatedAt(booking.getUpdatedDate())
                .build();
    }

    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();

        if (bookingDto.getBookingId() != null) {
            booking.setId(bookingDto.getBookingId());
        }

        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckoutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());

        if (bookingDto.getStatus() != null) {
            booking.setBookingStatus(bookingDto.getStatus());
        }

        if (bookingDto.getTotalPrice() != null) {
            booking.setPrice(bookingDto.getTotalPrice());
        }

        // Set room and guest from repositories
        if (bookingDto.getRoomId() != null) {
            booking.setRoom(roomRepository.findById(bookingDto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFindException("Room not found with ID: " + bookingDto.getRoomId())));
        }

        if (bookingDto.getGuestId() != null) {
            booking.setGuest(guestRepository.findById(bookingDto.getGuestId())
                    .orElseThrow(() -> new ResourceNotFindException("Guest not found with ID: " + bookingDto.getGuestId())));
        }

        return booking;
    }
}
