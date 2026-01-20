package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviewDtos = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable int reviewId) {
        ReviewDto getAll = reviewService.getReviewbyId(reviewId);
        return ResponseEntity.ok().body(getAll);
    }

    @PostMapping("/{hotelId}/{guestId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto,
                                                  @PathVariable Integer hotelId,
                                                  @PathVariable Integer guestId) {
        ReviewDto createReview = reviewService.addReview(reviewDto, hotelId, guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createReview);
    }

    @PutMapping("/{hotelId}/{guestId}/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Integer hotelId,
            @PathVariable Integer guestId,
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewDto reviewDto
    ) {
        ReviewDto updateReview = reviewService.updateReview(reviewId, reviewDto, hotelId, guestId);
        return ResponseEntity.ok().body(updateReview);
    }

    @DeleteMapping("/{hotelId}/{guestId}/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer hotelId,
            @PathVariable Integer guestId,
            @PathVariable Integer reviewId
    ) {
        reviewService.deleteReview(reviewId, guestId, hotelId);
        return ResponseEntity.noContent().build();
    }

}
