package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.dto.RoomDto;
import com.hotel.booking_system.model.Room;
import com.hotel.booking_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;


    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDto> saveRoom(@RequestBody RoomDto roomDto) {
        RoomDto created = roomService.addRoom(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<RoomDto>> getRoomByHotelNumber(@PathVariable Integer id) {
        List<RoomDto> rooms = roomService.getRoomsByHotelNumber(id);
        if (rooms == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(rooms);

        }
    }

    @GetMapping("/get/allrooms")
    public ResponseEntity<List<RoomDto>> getAllRooms(@RequestParam(required = false) Integer hotelNumber) {
        List<RoomDto> getAllRooms = roomService.getAllRooms(hotelNumber);
        if (getAllRooms.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return ResponseEntity.ok(getAllRooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Integer id, @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);

    }
}
