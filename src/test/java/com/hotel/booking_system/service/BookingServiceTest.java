package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.enums.BookingStatus;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.InvalidBookingStatusException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.BookingDtoMapper;
import com.hotel.booking_system.mapper.RoomDtoMapper;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Guest;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    private BookingDtoMapper bookingDtoMapper;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private Room room;
    private Hotel hotel;
    private Guest guest;


    @BeforeEach
    public void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setRoomId(10);
        bookingDto.setGuestId(20);
        bookingDto.setCheckInDate(LocalDate.now().plusDays(1));
        bookingDto.setCheckOutDate(LocalDate.now().plusDays(4));
        bookingDto.setNumberOfGuests(2);


        hotel = new Hotel();
        hotel.setId(1);

        guest = new Guest();
        guest.setId(20);
        guest.setFirstName("John");
        guest.setLastName("Doe");

        room = mock(Room.class);
        lenient().when(room.getHotel()).thenReturn(hotel);
        lenient().when(room.getMaxGuests()).thenReturn(5);
        lenient().when(room.getPricePerNight()).thenReturn(100.0);



        booking = new Booking();
        booking.setId(null);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckoutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setBookingStatus(BookingStatus.CONFIRMED);

    }

    @Test
    void addBookinSuccessAndReturnDto() {
        // Given -- cfare thirret ne metode dhe cfare duhet te ktheje

        // si fillim repository duhet te gjej dhomen
        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                eq(bookingDto.getGuestId()),
                eq(hotel.getId()),
                eq(bookingDto.getCheckOutDate())
                // false nuk ka rezervime te vjetra
        )).thenReturn(false);

        when(roomRepository.findById(bookingDto.getRoomId())).thenReturn(Optional.of(room));
        when(guestRepository.findById(bookingDto.getGuestId())).thenReturn(Optional.of(guest));


        // GIVEN: Mapper konverton DTO në Booking
        when(bookingDtoMapper.fromDto(bookingDto)).thenReturn(booking);

        // GIVEN: Mapper konverton Booking në DTO për rezultat
        when(bookingDtoMapper.apply(booking)).thenReturn(
                BookingDto.builder()
                        .roomId(bookingDto.getRoomId())
                        .guestId(bookingDto.getGuestId())
                        .checkInDate(bookingDto.getCheckInDate())
                        .checkOutDate(bookingDto.getCheckOutDate())
                        .numberOfGuests(bookingDto.getNumberOfGuests())
                        .status(BookingStatus.CONFIRMED)
                        .build()
        );

        when(bookingRepository.findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(
                eq(bookingDto.getRoomId()),
                eq(bookingDto.getCheckInDate()),
                eq(bookingDto.getCheckOutDate())
        )).thenReturn(List.of());


        // WHEN: Thirret metoda addBooking
        BookingDto result = bookingService.addBooking(bookingDto);

        // THEN: Verifikojmë rezultat
        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        assertEquals(2, result.getNumberOfGuests());

        verify(roomRepository, atLeast(1)).findById(eq(bookingDto.getRoomId()));
        verify(bookingRepository, times(1))
                .findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(
                        eq(bookingDto.getRoomId()),
                        eq(bookingDto.getCheckInDate()),
                        eq(bookingDto.getCheckOutDate())
                );
        verify(bookingRepository, times(1))
                .existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                        eq(bookingDto.getGuestId()),
                        eq(hotel.getId()),
                        eq(bookingDto.getCheckOutDate())
                );
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingDtoMapper, times(1)).fromDto(bookingDto);
        verify(bookingDtoMapper, times(1)).apply(booking);

    }

    @Test
    void addBookingThrowsBadRequestWhenCheckInPast(){
        //Given
        bookingDto.setCheckInDate(LocalDate.now().minusDays(1));
        bookingDto.setCheckOutDate(LocalDate.now().plusDays(2));

        //when + then
        assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));

    }

    @Test
    void addBooking_ShouldThrowDuplicateResourceException_WhenGuestHasOldBooking() {
        // GIVEN: RoomRepository gjen dhomën
        when(roomRepository.findById(bookingDto.getRoomId())).thenReturn(Optional.of(room));
        when(guestRepository.findById(bookingDto.getGuestId())).thenReturn(Optional.of(guest));

        when(bookingDtoMapper.fromDto(bookingDto)).thenReturn(booking);

        // GIVEN: Nuk ka rezervime të tjera për dhomën në këtë periudhë
        when(bookingRepository.findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(anyInt(), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());

        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(anyInt(), anyInt(), any(LocalDate.class))).thenReturn(true);


        // WHEN and  THEN
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> bookingService.addBooking(bookingDto));
        assertTrue(exception.getMessage().contains("Guest ka rezervime të mëparshme në këtë hotel."));

    }


        @Test
        void addBooking_throwsBadRequest_whenCheckOutBeforeCheckIn() {
            // GIVEN
            bookingDto.setCheckInDate(LocalDate.now().plusDays(5));
            bookingDto.setCheckOutDate(LocalDate.now().plusDays(2));

            // WHEN + THEN
            assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto));

            verify(bookingRepository, never()).save(any());
        }

        @Test
        void addBooking_throwsResourceNotFound_whenRoomNotFound() {
            // GIVEN
            when(roomRepository.findById(bookingDto.getRoomId()))
                    .thenReturn(Optional.empty());

            // WHEN + THEN
            assertThrows(ResourceNotFindException.class, () -> bookingService.addBooking(bookingDto));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void addBooking_throwsBadRequest_whenRoomCapacityExceeded() {
            // GIVEN
            bookingDto.setNumberOfGuests(6); // > maxGuests(5)
            when(roomRepository.findById(bookingDto.getRoomId()))
                    .thenReturn(Optional.of(room));

            // mapper nuk ka rëndësi këtu, sepse do bjerë te validateRoomCapacity përpara save
            // WHEN + THEN
            assertThrows(BadRequestException.class, () -> bookingService.addBooking(bookingDto));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void addBooking_throwsInvalidBookingStatus_whenCancelled() {
            // GIVEN
            bookingDto.setStatus(BookingStatus.CANCELLED);
            when(bookingDtoMapper.fromDto(bookingDto)).thenReturn(booking);
            when(roomRepository.findById(bookingDto.getRoomId()))
                    .thenReturn(Optional.of(room));
            when(guestRepository.findById(bookingDto.getGuestId()))
                    .thenReturn(Optional.of(guest));

            // validateBookingStatus happens after status is set from DTO
            // WHEN + THEN
            assertThrows(InvalidBookingStatusException.class, () -> bookingService.addBooking(bookingDto));


            verify(bookingRepository, never()).save(any());
        }

        @Test
        void addBooking_throwsDuplicateResource_whenGuestHasOldBookingInSameHotel() {
            // GIVEN
            when(roomRepository.findById(bookingDto.getRoomId()))
                    .thenReturn(Optional.of(room));
            when(guestRepository.findById(bookingDto.getGuestId()))
                    .thenReturn(Optional.of(guest));

            when(bookingRepository.findByRoom_IdAndCheckInDateLessThanAndCheckoutDateGreaterThan(
                    anyInt(), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(List.of());

            when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                    eq(bookingDto.getGuestId()),
                    eq(hotel.getId()),
                    eq(bookingDto.getCheckOutDate())
            )).thenReturn(true);

            when(bookingDtoMapper.fromDto(bookingDto)).thenReturn(booking);

            // WHEN + THEN
            assertThrows(DuplicateResourceException.class, () -> bookingService.addBooking(bookingDto));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        void findAllBooking_returnsList_whenNotEmpty() {
            // GIVEN
            Booking b1 = new Booking();
            Booking b2 = new Booking();
            when(bookingRepository.findAll()).thenReturn(List.of(b1, b2));

            BookingDto d1 = BookingDto.builder().roomId(1).guestId(1).build();
            BookingDto d2 = BookingDto.builder().roomId(2).guestId(2).build();

            when(bookingDtoMapper.apply(b1)).thenReturn(d1);
            when(bookingDtoMapper.apply(b2)).thenReturn(d2);

            // WHEN
            List<BookingDto> result = bookingService.findAllBooking();

            // THEN
            assertEquals(2, result.size());
            verify(bookingRepository, times(1)).findAll();
        }

        @Test
        void findAllBooking_returnsEmptyList_whenEmpty() {
            // GIVEN
            when(bookingRepository.findAll()).thenReturn(List.of());

            // WHEN
            List<BookingDto> result = bookingService.findAllBooking();

            // THEN
            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.size());
            verify(bookingRepository, times(1)).findAll();
    }
}
