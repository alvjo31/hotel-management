package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/guests/{guestId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviewDtos = reviewService.getAllReviews();
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable int id) {
        ReviewDto getAll = reviewService.getReviewbyId(id);
        return ResponseEntity.ok().body(getAll);
    }


    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto, @PathVariable Integer hotelId, @PathVariable Integer guestId) {
        ReviewDto createReview = reviewService.addReview(reviewDto, hotelId, guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Integer hotelId,
            @PathVariable Integer guestId,
            @PathVariable Integer reviewId,
            @RequestBody ReviewDto reviewDto
    ) {
        ReviewDto updateReview = reviewService.updateReview(reviewId, reviewDto, hotelId, guestId);
        return ResponseEntity.ok().body(updateReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer hotelId,
            @PathVariable Integer guestId,
            @PathVariable Integer reviewId
    ) {
        reviewService.deleteReview(reviewId, guestId, hotelId);
        return ResponseEntity.noContent().build();
    }

}
