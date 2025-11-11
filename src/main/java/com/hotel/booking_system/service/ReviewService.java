package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.ReviewDto;
import com.hotel.booking_system.mapper.ReviewDtoMapper;
import com.hotel.booking_system.model.Review;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.ReviewRepository;
import com.hotel.booking_system.repository.RoomRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private ReviewDtoMapper reviewDtoMapper;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewDtoMapper reviewDtoMapper, HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewDtoMapper = reviewDtoMapper;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }



    private ReviewDto addReview(ReviewDto reviewDto) {
        validateReview(reviewDto);
        validateHotelAndGuest();
        Review review = reviewDtoMapper.fromDto(reviewDto);
      Review saved  = reviewRepository.save(review);
        return reviewDtoMapper.apply(saved);
    }


private void validateReview(ReviewDto reviewDto) {
    if (reviewDto.getRating() == null) {
        throw new IllegalArgumentException("Vleresimi nuk mund te jete bosh");
    }if (reviewDto.getRating()<0 || reviewDto.getRating() > 5) {
        throw new IllegalArgumentException("Vleresimi duhet te jete midis 1-5");
    }if (reviewDto.getComment() == null || reviewDto.getComment().isBlank()) {
        throw new IllegalArgumentException("Komenti nuk mund te jete bosh");
    }if (reviewDto.getComment().length()>1000){
        throw new IllegalArgumentException("Komenti nuk duhet te jete me shume se 1000 karaktere");
       }if (reviewDto.getDate() == null && reviewDto.getDate().after(new Date())) {
        throw new IllegalArgumentException("Data nuk mund te jete en te ardhmen");
    }
   }

   private void validateHotelAndGuest(Integer hotelId, Integer guestId) {

   }
}
