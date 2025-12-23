package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.ReviewDtoMapper;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Review;
import com.hotel.booking_system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewDtoMapper reviewDtoMapper;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewDto reviewDto;
    private Review review;
    private Booking booking;

    private Integer hotelId = 1;
    private Integer guestId = 2;

    @BeforeEach
    void setUp() {
        reviewDto = new ReviewDto();
        reviewDto.setRating(5.0);
        reviewDto.setComment("Great hotel!");
        reviewDto.setDate(new Date());

        review = new Review();
        review.setRating(5.0);
        review.setComment("Great hotel!");
        review.setDate(new Date());

        booking = new Booking();
        booking.setCheckoutDate(LocalDate.now().minusDays(10));
    }

    // -------------------------
    // ADD REVIEW - SUCCESS
    // -------------------------
    @Test
    void addReview_shouldReturnReviewDto_whenValid() {
        // GIVEN
        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(guestRepository.existsById(guestId)).thenReturn(true);

        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                eq(guestId), eq(hotelId), any(LocalDate.class)
        )).thenReturn(true);

        when(reviewRepository.existsByGuestIdAndHotelId(guestId, hotelId)).thenReturn(false);

        when(bookingRepository.findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(
                guestId, hotelId
        )).thenReturn(Optional.of(booking));

        when(reviewDtoMapper.fromDto(reviewDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewDtoMapper.apply(review)).thenReturn(reviewDto);

        // WHEN
        ReviewDto result = reviewService.addReview(reviewDto, hotelId, guestId);

        // THEN
        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).save(review);
    }

    // -------------------------
    // INVALID RATING
    // -------------------------
    @Test
    void addReview_shouldThrowBadRequest_whenRatingInvalid() {
        reviewDto.setRating(10.0);

        assertThrows(BadRequestException.class,
                () -> reviewService.addReview(reviewDto, hotelId, guestId));

        verify(reviewRepository, never()).save(any());
    }

    // -------------------------
    // HOTEL NOT FOUND
    // -------------------------
    @Test
    void addReview_shouldThrowResourceNotFound_whenHotelNotExists() {
        when(hotelRepository.existsById(hotelId)).thenReturn(false);

        assertThrows(ResourceNotFindException.class,
                () -> reviewService.addReview(reviewDto, hotelId, guestId));
    }

    // -------------------------
    // GUEST DID NOT STAY
    // -------------------------
    @Test
    void addReview_shouldThrowBadRequest_whenGuestDidNotStayInHotel() {
        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(guestRepository.existsById(guestId)).thenReturn(true);

        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                eq(guestId), eq(hotelId), any(LocalDate.class)
        )).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> reviewService.addReview(reviewDto, hotelId, guestId));
    }

    // -------------------------
    // DUPLICATE REVIEW
    // -------------------------
    @Test
    void addReview_shouldThrowDuplicate_whenAlreadyReviewed() {
        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(guestRepository.existsById(guestId)).thenReturn(true);

        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                eq(guestId), eq(hotelId), any(LocalDate.class)
        )).thenReturn(true);

        when(reviewRepository.existsByGuestIdAndHotelId(guestId, hotelId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> reviewService.addReview(reviewDto, hotelId, guestId));
    }

    // -------------------------
    // REVIEW TOO LATE (> 365 DAYS)
    // -------------------------
    @Test
    void addReview_shouldThrowBadRequest_whenReviewTooLate() {
        booking.setCheckoutDate(LocalDate.now().minusDays(400));

        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(guestRepository.existsById(guestId)).thenReturn(true);

        when(bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(
                eq(guestId), eq(hotelId), any(LocalDate.class)
        )).thenReturn(true);

        when(reviewRepository.existsByGuestIdAndHotelId(guestId, hotelId)).thenReturn(false);

        when(bookingRepository.findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(
                guestId, hotelId
        )).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> reviewService.addReview(reviewDto, hotelId, guestId));
    }

    // -------------------------
    // GET REVIEW BY ID
    // -------------------------
    @Test
    void getReviewById_shouldReturnDto_whenExists() {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewDtoMapper.apply(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.getReviewbyId(1);

        assertNotNull(result);
    }

    // -------------------------
    // GET ALL REVIEWS
    // -------------------------
    @Test
    void getAllReviews_shouldReturnList() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewDtoMapper.apply(review)).thenReturn(reviewDto);

        List<ReviewDto> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
    }

    // -------------------------
    // DELETE REVIEW
    // -------------------------
    @Test
    void deleteReview_shouldThrow_whenNotExists() {
        when(reviewRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFindException.class,
                () -> reviewService.deleteReview(1));
    }
}
