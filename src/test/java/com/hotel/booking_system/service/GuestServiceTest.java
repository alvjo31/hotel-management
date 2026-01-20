package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.GuestDtoMapper;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Guest;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private GuestDtoMapper guestDtoMapper;
    @InjectMocks
    private GuestService guestService;

    private Guest guest;
    private GuestDto guestDto;

    @BeforeEach
    public void setUp() {
        guest = new Guest();
        guest.setId(1);
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@example.com");
        guest.setPhone("+1234567890");
        guest.setBookings(new ArrayList<>());

        guestDto = new GuestDto();
        guestDto.setGuestId(1);
        guestDto.setFirstName("John");
        guestDto.setLastName("Doe");
        guestDto.setEmail("john@example.com" );
        guestDto.setPhone("+1234567890");
    }

    @Test
    void addGuest_ShouldSaveGuest_WhenValid() {
        // GIVEN
        when(guestRepository.existsByEmail(guestDto.getEmail())).thenReturn(false);
        when(guestDtoMapper.fromDto(guestDto)).thenReturn(guest);
        when(guestRepository.save(guest)).thenReturn(guest);
        when(guestDtoMapper.apply(guest)).thenReturn(guestDto);

        // WHEN
        GuestDto result = guestService.addGuest(guestDto);

        // THEN
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(guestRepository, times(1)).save(guest);
    }

    // -------------------
    // TEST: Exception kur email ekziston
    // -------------------
    @Test
    void addGuest_ShouldThrowDuplicateResource_WhenEmailExists() {
        // GIVEN
        when(guestRepository.existsByEmail(guestDto.getEmail())).thenReturn(true);

        // WHEN + THEN
        assertThrows(DuplicateResourceException.class, () -> guestService.addGuest(guestDto));
        verify(guestRepository, never()).save(any());
    }

    // -------------------
    // TEST: Exception për emër të shkurtër ose bosh
    // -------------------
    @Test
    void addGuest_ShouldThrowBadRequest_WhenFirstNameEmpty() {
        guestDto.setFirstName("");
        assertThrows(BadRequestException.class, () -> guestService.addGuest(guestDto));
    }

    @Test
    void addGuest_ShouldThrowBadRequest_WhenLastNameEmpty() {
        guestDto.setLastName("");
        assertThrows(BadRequestException.class, () -> guestService.addGuest(guestDto));
    }

    // -------------------
    // TEST: gjej guest me ID
    // -------------------
    @Test
    void findGuestById_ShouldReturnGuestDto_WhenExists() {
        when(guestRepository.findById(1)).thenReturn(Optional.of(guest));
        when(guestDtoMapper.apply(guest)).thenReturn(guestDto);

        GuestDto result = guestService.findGuestDtoById(1);

        assertNotNull(result);
        assertEquals(1, result.getGuestId());
    }

    @Test
    void findGuestById_ShouldThrowResourceNotFound_WhenNotExists() {
        when(guestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFindException.class, () -> guestService.findGuestDtoById(1));
    }

    // -------------------
    // TEST: Update guest
    // -------------------
    @Test
    void updateGuest_ShouldThrowDuplicate_WhenHasActiveBooking() {
        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        guest.getBookings().add(booking);

        when(guestRepository.findById(guestDto.getGuestId())).thenReturn(Optional.of(guest));

        assertThrows(DuplicateResourceException.class,
                () -> guestService.updateGuests(guestDto.getGuestId(), guestDto));
    }

    // -------------------
    // TEST: Gjej të gjithë guest
    // -------------------
    @Test
    void findAllGuests_ShouldReturnList() {
        List<Guest> guests = List.of(guest);
        when(guestRepository.findAll()).thenReturn(guests);
        when(guestDtoMapper.apply(guest)).thenReturn(guestDto);

        List<GuestDto> result = guestService.findAllGuests();
        assertEquals(1, result.size());
    }

    // -------------------
    // TEST: Delete guest që nuk ekziston
    // -------------------
    @Test
    void deleteGuest_ShouldThrow_WhenNotExists() {
        when(guestRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFindException.class, () -> guestService.deleteRoom(1));
    }

}
