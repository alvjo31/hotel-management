package com.hotel.booking_system.repository;

import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Guest;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Guest guest;
    private Hotel hotel;
    private Room room;
    private Booking booking;

    @BeforeEach
    void setup() {

        // --- Guest ---
        guest = new Guest();
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@example.com");
        guest.setPhone("123456789");
        guest.setBookings(new ArrayList<>());
        guest.setReviews(new ArrayList<>());
        guest = guestRepository.save(guest);

        // --- Hotel ---
        hotel = new Hotel();
        hotel.setHotelName("Test Hotel");
        hotel.setHotelAddress("123 Main Street");
        hotel.setHotelCity("CityName");
        hotel.setHotelDescription("Nice hotel for testing");
        hotel.setHotelPhone(123456789);
        hotel.setHotelEmail("test@hotel.com");
        hotel = hotelRepository.save(hotel);

        // --- Room ---
        room = new Room();
        room.setNumber(101);
        room.setCapacity(2);
        room.setPrice(150.0);
        room.setBookingStatus(BookingStatus.CONFIRMED);
        room.setMaxGuests(2);
        room.setPricePerNight(150.0);
        room.setHotel(hotel);
        roomRepository.save(room);


        booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 12, 30));
        booking.setCheckoutDate(LocalDate.of(2026, 1, 2));
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setNumberOfGuests(room.getMaxGuests());
        booking.setPrice(room.getPricePerNight() * room.getMaxGuests());

    }

    @Test
    void testSaveBooking() {
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2026, 12, 30));
        booking.setCheckoutDate(LocalDate.of(2027, 1, 2));
        booking.setPrice(200.0);
        // --- WHEN ---
        Booking saved = bookingRepository.save(booking);
        // --- THEN ---
        assertNotNull(saved.getId());
        assertEquals(guest.getId(), saved.getGuest().getId());
        assertEquals(room.getId(), saved.getRoom().getId());
    }

    @Test
    void testFindByRoomIdAndDateOverlap() {
        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 12, 30));
        booking.setCheckoutDate(LocalDate.of(2026, 1, 2));
        booking.setPrice(200.0);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setNumberOfGuests(2);
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(
                room.getId(),
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2026, 1, 1)
        );

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getCheckInDate(), result.get(0).getCheckInDate());
    }

    @Test
    void testExistsByGuestAndHotelBeforeDate() {
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 12, 20));
        booking.setCheckoutDate(LocalDate.of(2025, 12, 25));
        booking.setPrice(150.0);
        bookingRepository.save(booking);

        boolean exists = bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                guest.getId(),
                hotel.getId(),
                LocalDate.of(2025, 12, 26)
        );

        assertTrue(exists);
    }


    // kontroll ku eshte gabimi
    @Test
    void testFindTopByGuestAndHotel() {
        Booking oldBooking = new Booking();
        oldBooking.setGuest(guest);
        oldBooking.setRoom(room);
        oldBooking.setCheckInDate(LocalDate.of(2026, 01, 20));
        oldBooking.setCheckoutDate(LocalDate.of(2026, 03, 25));
        oldBooking.setPrice(150.0);
        oldBooking.setBookingStatus(BookingStatus.CONFIRMED);
        oldBooking.setNumberOfGuests(2);
        bookingRepository.save(oldBooking);

        // --- Booking i fundit ---
        Booking lastBooking = new Booking();
        lastBooking.setGuest(guest);
        lastBooking.setRoom(room);
        lastBooking.setCheckInDate(LocalDate.of(2026, 05, 26));
        lastBooking.setCheckoutDate(LocalDate.of(2026, 07, 30));
        lastBooking.setPrice(200.0);
        lastBooking.setBookingStatus(BookingStatus.CONFIRMED);
        lastBooking.setNumberOfGuests(3);
        bookingRepository.save(lastBooking);

        Optional<Booking> top = bookingRepository.findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(
                guest.getId(), hotel.getId()
        );

        assertTrue(top.isPresent());
        assertEquals(lastBooking.getCheckoutDate(), top.get().getCheckoutDate());
    }
}
