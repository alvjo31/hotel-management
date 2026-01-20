package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.ReviewDtoMapper;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.model.Guest;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.model.Review;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReviewService {
    private final GuestRepository guestRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewDtoMapper reviewDtoMapper;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewDtoMapper reviewDtoMapper, HotelRepository hotelRepository, GuestRepository guestRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewDtoMapper = reviewDtoMapper;
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewDto addReview(ReviewDto reviewDto, Integer hotelId, Integer guestId) {
        validateReview(reviewDto);


        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFindException("Hotel not found"));
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFindException("Guest not found"));

        Booking booking = bookingRepository.findTopByGuest_IdAndRoom_Hotel_IdOrderByCheckoutDateDesc(guestId, hotelId)
                .orElseThrow(() -> new ResourceNotFindException("Booking not found"));
        if (booking.getCheckoutDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Nuk mund te lesh review perpara check out.");
        }
        validateDuplicateReview(guestId ,hotelId);
        validateReviewTiming(guestId, hotelId);

        Review review = reviewDtoMapper.fromDto(reviewDto);
        review.setGuest(guest);
        review.setHotel(hotel);
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
        if (reviewDto.getReviewDate() == null) {
            throw new BadRequestException("Data nuk mund te jete bosh");
        }

        if (reviewDto.getReviewDate().isAfter(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Data nuk mund te jete ne te ardhmen");
        }

    }

    private void validateDuplicateReview(Integer guestId, Integer hotelId) {
        boolean alreadyReviewed =
                reviewRepository.existsByGuestIdAndHotelId(guestId, hotelId);
        if (alreadyReviewed) {
            throw new DuplicateResourceException(
                    "Guest ka lënë tashmë një review për këtë hotel"
            );
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
        // perditesimin e vlerave
        if (!review.getGuest().getId().equals(guestId)) {
            throw new BadRequestException("Nuk lejohet të modifikoni këtë review");
        }

        if (!review.getHotel().getId().equals(hotelId)) {
            throw new BadRequestException("Review nuk i përket këtij hoteli");
        }

        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        // Convert LocalDateTime to Date for entity
        if (reviewDto.getReviewDate() != null) {
            review.setDate(java.util.Date.from(reviewDto.getReviewDate()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()));
        }
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
    public void deleteReview(Integer reviewId, Integer guestId, Integer hotelId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFindException("Review not found"));

        if (!review.getGuest().getId().equals(guestId)) {
            throw new BadRequestException("Nuk lejohet të fshini këtë review");
        }

        if (!review.getHotel().getId().equals(hotelId)) {
            throw new BadRequestException("Review nuk i përket këtij hoteli");
        }

        reviewRepository.delete(review);
    }

}
