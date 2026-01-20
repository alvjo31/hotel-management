package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.BookingDto;
import com.hotel.booking_system.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookingDto> getAll = bookingService.findAllBooking();
        return ResponseEntity.ok(getAll);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<BookingDto> saveBooking(@Valid @RequestBody BookingDto bookingDto) {
        BookingDto saved = bookingService.addBooking(bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Integer id, @Valid @RequestBody BookingDto bookingDto) {
        BookingDto updated = bookingService.updateBookingDto(id, bookingDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/getbooksbyid/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<BookingDto> getBookingByID(@PathVariable Integer id) {
        BookingDto bookingDto = bookingService.getBookingDtoById(id);
        return ResponseEntity.ok(bookingDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) {
        bookingService.findBookingDtoById(id);
        return ResponseEntity.noContent().build();
    }
}
