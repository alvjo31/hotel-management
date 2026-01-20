package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RoomDto> saveRoom(@PathVariable Integer hotelId, @Valid @RequestBody RoomDto roomDto) {
        RoomDto created = roomService.addRoomToHotel(roomDto, hotelId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<RoomDto>> getRoomByHotelNumber(@PathVariable Integer id) {
        List<RoomDto> rooms = roomService.getRoomsByHotelNumber(id);
        if (rooms == null || rooms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(rooms);
        }
    }

    @GetMapping("/get/allrooms")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<RoomDto>> getAllRooms(@RequestParam(required = false) Integer hotelNumber) {
        List<RoomDto> getAllRooms = roomService.getAllRooms(hotelNumber);
        if (getAllRooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(getAllRooms);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
