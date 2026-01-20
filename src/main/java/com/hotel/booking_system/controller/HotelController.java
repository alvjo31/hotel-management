package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/get/hotels")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<HotelDto>> getAllHotels() {
       List<HotelDto> hotels = hotelService.getAllHotels();
       return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable int id) {
        HotelDto dto = hotelService.getHotelById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelDto hotelDto) {
        HotelDto created = hotelService.addHotel(hotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable int id, @Valid @RequestBody HotelDto hotelDto) {
        HotelDto updated = hotelService.updateHotel(id, hotelDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
