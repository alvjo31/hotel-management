package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.mapper.BookingDtoMapper;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    //first comment
    private BookingRepository bookingRepository;
    private BookingDtoMapper bookingDtoMapper;
    private RoomRepository roomRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingDtoMapper bookingDtoMapper, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.roomRepository = roomRepository;
    }

    public BookingDto addBooking(BookingDto bookingDto) {
        validateDates(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        isRoomAvailable(bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        checkNumberOfGuets(bookingDto.getNumberOfGuests());
        validateDates(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        validateRoomCapacity(bookingDto.getRoomId(), bookingDto.getNumberOfGuests());
        // Krijimi i objektit Booking nga BookingDto dhe validimi i statusit
        Booking booking = bookingDtoMapper.fromDto(bookingDto); // Krijo një Booking nga BookingDto
        validateBookingStatus(booking); // Kontrollo statusin e rezervimit
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + bookingDto.getRoomId()));

        long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        double price = calculatePrice(room, bookingDto.getNumberOfGuests(), numberOfNights);

        // Vendos çmimin dhe statusin e rezervimit
        booking.setPrice(price);
        booking.setBookingStatus(bookingDto.getBookingStatus() != null
                        ? bookingDto.getBookingStatus()
                        : BookingStatus.CONFIRMED);
        booking.setCreatedDate(java.time.LocalDateTime.now());
        booking.setUpdatedDate(java.time.LocalDateTime.now());

        // Ruaj rezervimin në bazën e të dhënave
        bookingRepository.save(booking);

        // Kthejmë një BookingDto të ri me informacionet e rezervimit
        return bookingDtoMapper.apply(booking);

    }


    // Find bookings for the room that overlap with the given check-in and check-out dates
    public boolean isRoomAvailable(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(roomId, checkInDate, checkOutDate);

        // If there are any overlapping bookings, the room is not available
        return overlappingBookings.isEmpty();
    }

    public void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date duhet te jete ne te ardhmen.");
        }
        if (checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out date duhet te jete pas check-in date.");
        }
    }

    public void checkNumberOfGuets(int numberOfGuests) {
        if (numberOfGuests <= 0 || numberOfGuests > 10) {
            throw new IllegalArgumentException("Numri i guests duhet te jete midis 1 dhe 10.");
        }
    }

    public void validateRoomCapacity(Integer roomId, int numberOfGuests) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new IllegalArgumentException("Room with ID " + roomId + " not found.");
        }
        if (room.get().getMaxGuests() < numberOfGuests) {
            throw new IllegalArgumentException("Room capacity exceeded.");
        }
    }

    public void validateBookingStatus(Booking booking) {
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a cancelled booking.");
        }
    }

    public double calculatePrice(Room room, int numberOfGuests, long numberOfNights) {
        double basePrice = room.getPricePerNight();
        double totalPrice = basePrice * numberOfNights * numberOfGuests;
        return totalPrice;
    }

    public BookingDto getBookingDtoById(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById((int) id);
        return optionalBooking.map(bookingDtoMapper).orElse(null);
    }

    public BookingDto updateBookingDto(Integer id, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking with ID " + id + " not found."));
        booking.setRoom(roomRepository.findById(bookingDto.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Room with ID " + id + " not found.")));
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setPrice(bookingDto.getPrice());
        booking.setBookingStatus(
                bookingDto.getBookingStatus() != null
                        ? bookingDto.getBookingStatus()
                        : BookingStatus.CONFIRMED
        );

        booking.setUpdatedDate(java.time.LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingDtoMapper.apply(booking);
    }

    public void deleteBookingDtoById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking me ID " + id + " nuk ekziston.");
        }
        bookingRepository.deleteById(id);
    }

    public BookingDto findBookingDtoById(int id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        return bookingDtoMapper.apply(optionalBooking.get());
    }

}
