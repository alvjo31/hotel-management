package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.InvalidBookingStatusException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.mapper.BookingDtoMapper;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import com.hotel.booking_system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;


    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingDtoMapper bookingDtoMapper, RoomRepository roomRepository, GuestRepository guestRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @Transactional
    public BookingDto addBooking(BookingDto bookingDto) {

        validateDates(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        checkNumberOfGuets(bookingDto.getNumberOfGuests());
        validateRoomCapacity(bookingDto.getRoomId(), bookingDto.getNumberOfGuests());
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFindException("Room not found with ID: " + bookingDto.getRoomId()));
        isRoomAvailable(bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());// Krijimi i objektit Booking nga BookingDto dhe validimi i statusit
        Booking booking = bookingDtoMapper.fromDto(bookingDto);
        booking.setRoom(room);
        booking.setGuest(
                guestRepository.findById(bookingDto.getGuestId())
                        .orElseThrow(() -> new ResourceNotFindException("Guest not found"))
        );
        long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        double price = calculatePrice(room, bookingDto.getNumberOfGuests(), numberOfNights);
        booking.setBookingStatus(bookingDto.getStatus() != null
                ? bookingDto.getStatus()
                : BookingStatus.CONFIRMED);
        booking.setCreatedDate(java.time.LocalDateTime.now());
        booking.setUpdatedDate(java.time.LocalDateTime.now());
// Krijo një Booking nga BookingDto
        validateBookingStatus(booking);
        // Kontrollo statusin e rezervimit

        Integer hotelId = room.getHotel().getId();
        // 3. Kontrollo historikun e guest
        boolean hasOldBooking = bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                bookingDto.getGuestId(),
                hotelId,
                bookingDto.getCheckOutDate()
        );

        if (hasOldBooking) {
            throw new DuplicateResourceException("Guest ka rezervime të mëparshme në këtë hotel.");
        }


        // Ruaj rezervimin në bazën e të dhënave
        bookingRepository.save(booking);

        // Kthejmë një BookingDto të ri me informacionet e rezervimit
        return bookingDtoMapper.apply(booking);

    }


    public boolean isRoomAvailable(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Booking> overlappingBookings = bookingRepository.findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(roomId, checkInDate, checkOutDate);

        // If there are any overlapping bookings, the room is not available
        return overlappingBookings.isEmpty();
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date duhet te jete ne te ardhmen.");
        }
        if (checkOutDate.isBefore(checkInDate)) {
            throw new BadRequestException("Check-out date duhet te jete pas check-in date.");
        }
    }

    private void checkNumberOfGuets(int numberOfGuests) {
        if (numberOfGuests <= 0 || numberOfGuests > 10) {
            throw new BadRequestException("Numri i guests duhet te jete midis 1 dhe 10.");
        }
    }

    private void validateRoomCapacity(Integer roomId, int numberOfGuests) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new ResourceNotFindException("Room with ID " + roomId + " not found.");
        }
        if (room.get().getMaxGuests() < numberOfGuests) {
            throw new BadRequestException("Room capacity exceeded.");
        }
    }

    private void validateBookingStatus(Booking booking) {
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStatusException("Cannot modify a cancelled booking.");
        }
    }

    private double calculatePrice(Room room, int numberOfGuests, long numberOfNights) {
        double basePrice = room.getPricePerNight();
        double totalPrice = basePrice * numberOfNights * numberOfGuests;
        return totalPrice;
    }

    public BookingDto getBookingDtoById(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById((int) id);
        return optionalBooking.map(bookingDtoMapper).orElse(null);
    }

    @Transactional
    public BookingDto updateBookingDto(Integer id, BookingDto bookingDto) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFindException("Booking with ID " + id + " not found."));
        booking.setRoom(roomRepository.findById(bookingDto.getRoomId()).orElseThrow(() -> new ResourceNotFindException("Room with ID " + id + " not found.")));
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckoutDate(bookingDto.getCheckOutDate());
        booking.setPrice(bookingDto.getTotalPrice());
        booking.setBookingStatus(
                bookingDto.getStatus() != null
                        ? bookingDto.getStatus()
                        : BookingStatus.CONFIRMED
        );

        booking.setUpdatedDate(java.time.LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingDtoMapper.apply(booking);
    }

    @Transactional
    public void deleteBookingDtoById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFindException("Booking me ID " + id + " nuk ekziston.");
        }
        bookingRepository.deleteById(id);
    }

    public BookingDto findBookingDtoById(int id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        return bookingDtoMapper.apply(optionalBooking.get());
    }

    public List<BookingDto> findAllBooking() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingDtoMapper)
                .toList();
    }
}
