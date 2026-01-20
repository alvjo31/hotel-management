package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {
    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping("/get/allguests")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        List<GuestDto> guests = guestService.findAllGuests();
        return ResponseEntity.ok(guests);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<GuestDto> createGuest(@RequestBody @Valid GuestDto guestDto) {
        GuestDto createGuest = guestService.addGuest(guestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGuest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Integer id) {
        GuestDto guestById = guestService.findGuestDtoById(id);
        return ResponseEntity.ok(guestById);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Integer id, @Valid @RequestBody GuestDto guestDto) {
        GuestDto updated = guestService.updateGuests(id, guestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGuestById(@PathVariable Integer id) {
        guestService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

}
