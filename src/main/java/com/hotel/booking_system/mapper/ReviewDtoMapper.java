package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.model.Review;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

/**
 * Mapper for converting between Review entity and ReviewDto
 */
@Component
public class ReviewDtoMapper implements Function<Review, ReviewDto> {

    @Override
    public ReviewDto apply(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getDate() != null ?
                    convertDateToLocalDateTime(review.getDate()) : null)
                .hotelId(review.getHotel() != null ? review.getHotel().getId() : null)
                .hotelName(review.getHotel() != null ? review.getHotel().getHotelName() : null)
                .guestId(review.getGuest() != null ? review.getGuest().getId() : null)
                .guestName(review.getGuest() != null ?
                    review.getGuest().getFirstName() + " " + review.getGuest().getLastName() : null)
                .build();
    }

    public Review fromDto(ReviewDto reviewDto) {
        Review review = new Review();

        if (reviewDto.getReviewId() != null) {
            review.setId(reviewDto.getReviewId());
        }

        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        if (reviewDto.getReviewDate() != null) {
            review.setDate(convertLocalDateTimeToDate(reviewDto.getReviewDate()));
        } else {
            review.setDate(new Date()); // Set current date if not provided
        }

        return review;
    }

    /**
     * Convert Date to LocalDateTime
     */
    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Convert LocalDateTime to Date
     */
    private Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}

