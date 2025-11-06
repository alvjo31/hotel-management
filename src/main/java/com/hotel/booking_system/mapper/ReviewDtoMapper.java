package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Builder
public class ReviewDtoMapper implements Function<Review, ReviewDto> {

    // Metoda 'toDto' e përdorur për të transformuar Review në ReviewDto
    public ReviewDto apply(Review review) {
        return new ReviewDto(review.getRating(), review.getComment(), review.getDate());
    }

    public Review fromDto(ReviewDto reviewDto) {
        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setDate(reviewDto.getDate());
        return review;
    }

}

