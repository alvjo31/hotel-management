package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.ReviewDtoMapper;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Review;
import com.hotel.booking_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReviewService {
    private final GuestRepository guestRepository;
    private ReviewRepository reviewRepository;
    private ReviewDtoMapper reviewDtoMapper;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewDtoMapper reviewDtoMapper, HotelRepository hotelRepository, RoomRepository roomRepository, GuestRepository guestRepository,BookingRepository bookingRepository)  {
        this.reviewRepository = reviewRepository;
        this.reviewDtoMapper = reviewDtoMapper;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewDto addReview(ReviewDto reviewDto, Integer hotelId, Integer guestId) {
        validateReview(reviewDto);
        validateHotelAndGuest(hotelId, guestId);
        guestStayedInHotel(hotelId, guestId);
        validateNoplicateReview(guestId ,hotelId);
        validateReviewTiming(guestId,hotelId);
        Review review = reviewDtoMapper.fromDto(reviewDto);
        review.setGuest(guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFindException("Guest not found")));
        review.setHotel(hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFindException("Hotel not found")));
        Review saved = reviewRepository.save(review);
        return reviewDtoMapper.apply(saved);
    }


    private void validateReview(ReviewDto reviewDto) {
        if (reviewDto.getRating() == null) {
            throw new BadRequestException("Vleresimi nuk mund te jete bosh");
        }
        if (reviewDto.getRating() < 0 || reviewDto.getRating() > 5) {
            throw new BadRequestException("Vleresimi duhet te jete midis 1-5");
        }
        if (reviewDto.getComment() == null || reviewDto.getComment().isBlank()) {
            throw new BadRequestException("Komenti nuk mund te jete bosh");
        }
        if (reviewDto.getComment().length() > 1000) {
            throw new BadRequestException("Komenti nuk duhet te jete me shume se 1000 karaktere");
        }
        if (reviewDto.getDate() == null && reviewDto.getDate().after(new Date())) {
            throw new BadRequestException("Data nuk mund te jete en te ardhmen");
        }
    }

    private void validateHotelAndGuest(Integer hotelId, Integer guestId) {
        if (hotelId == null) {
            throw new BadRequestException("Hotel ID nuk mund te jete bosh");
        }

        if (guestId == null) {
            throw new BadRequestException("Guest ID nuk mund te jete bosh");
        }

        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFindException("Hotel me id " + hotelId + " nuk ekziston");
        }

        if (!guestRepository.existsById(guestId)) {
            throw new ResourceNotFindException("Guest me id " + guestId + " nuk ekziston");
        }
    }

    public void guestStayedInHotel(Integer guestId, Integer hotelId) {
        boolean stayedInHotel = bookingRepository.existsByGuest_IdAndRoom_Hotel_IdAndCheckoutDateBefore(guestId , hotelId ,LocalDate.now());
            if (!stayedInHotel) {
                throw new BadRequestException("Guest nuk mund te le review pa qendruar ne hotel");
            }

    }


    private void validateNoplicateReview(Integer guestId, Integer hotelId) {
        boolean alreadyReviewd = reviewRepository.existsByGuestIdAndHotelId(guestId, hotelId);
        if (alreadyReviewd) {
            throw new DuplicateResourceException("Keni lene nje review tashme");
        }
    }

    // ✅ Review Timing (brenda 365 ditëve nga checkout)
    private void validateReviewTiming(Integer guestId, Integer hotelId) {
        Booking booking = bookingRepository.findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(guestId, hotelId)
                .orElseThrow(() -> new ResourceNotFindException("Guest nuk ka qëndruar në këtë hotel."));

        long days = ChronoUnit.DAYS.between(booking.getCheckoutDate(), LocalDate.now());

        if (days > 365) {
            throw new BadRequestException("Nuk mund të lësh review më vonë se 365 ditë pas checkout-it.");
        }
    }


    @Transactional
    public ReviewDto updateReview(Integer reviewId, ReviewDto reviewDto, Integer hotelId, Integer guestId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFindException("Review not found"));
        validateReview(reviewDto);
        validateHotelAndGuest(hotelId, guestId);
        // perditesimin e vlerave

        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setDate(reviewDto.getDate());
        Review saved = reviewRepository.save(review);
        return reviewDtoMapper.apply(saved);
    }

    public ReviewDto getReviewbyId(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFindException("Review not found"));
        return reviewDtoMapper.apply(review);
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewDtoMapper)
                .toList();

    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFindException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }
}
