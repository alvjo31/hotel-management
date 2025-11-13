package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/get/booking")
    public ResponseEntity <List<BookingDto>>getAllBookings() {
        List<BookingDto> getAll = bookingService.findAllBooking();
        return ResponseEntity.ok(getAll);

    }

    @PostMapping
    public ResponseEntity <BookingDto> saveBooking(@RequestBody BookingDto bookingDto) {
        BookingDto saved = bookingService.addBooking(bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/update/booking")
    public ResponseEntity <BookingDto> updateBooking( @PathVariable Integer id ,@RequestBody BookingDto bookingDto) {
        BookingDto updated = bookingService.updateBookingDto(id, bookingDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/get/booksbyid")
    public ResponseEntity <BookingDto> getBookingByID(@RequestParam Integer id) {
        BookingDto bookingDto = bookingService.getBookingDtoById(id);
        return ResponseEntity.ok(bookingDto);
    }
}
