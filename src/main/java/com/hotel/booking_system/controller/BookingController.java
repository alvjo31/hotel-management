package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.model.Booking;
import com.hotel.booking_system.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {
    private final BookingService bookingService;

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

    @PutMapping("/{id}")
    public ResponseEntity <BookingDto> updateBooking( @PathVariable Integer id ,@RequestBody BookingDto bookingDto) {
        BookingDto updated = bookingService.updateBookingDto(id, bookingDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/getbooksbyid/{id}")
    public ResponseEntity <BookingDto> getBookingByID(@PathVariable Integer id) {
        BookingDto bookingDto = bookingService.getBookingDtoById(id);
        return ResponseEntity.ok(bookingDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity <BookingDto> deleteBooking(@PathVariable Integer id) {
        BookingDto bookingDto = bookingService.findBookingDtoById(id);
        return ResponseEntity.ok(bookingDto);
    }
}
