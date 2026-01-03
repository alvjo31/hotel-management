package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.service.GuestService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        List<GuestDto> guests = guestService.findAllGuests();
        return ResponseEntity.ok(guests);

    }

    @PostMapping
    public ResponseEntity<GuestDto> createGuest(@RequestBody @Valid GuestDto guestDto) throws BadRequestException {
        GuestDto createGuest = guestService.addGuest(guestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGuest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Integer id) {
        GuestDto guestById = guestService.findGuestDtoById(id);

        return ResponseEntity.ok(guestById);
    }

    @PutMapping("/{id}")
    public GuestDto updateGuest(@PathVariable Integer id, @RequestBody GuestDto guestDto) throws BadRequestException {
        GuestDto updated = guestService.updateGuests(id ,guestDto);
        return updated;
    }

    @DeleteMapping("{id}")
    public void deleteGuestById(@PathVariable Integer id) {
        guestService.deleteRoom(id);
    }

}
