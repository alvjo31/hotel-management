package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/get/hotels")
    public List<HotelDto> getAllHotels() {
       return hotelService.getAllHotels();
    }


    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable int id) {
        HotelDto dto = hotelService.getHotelById(id);
        return ResponseEntity.ok(dto);
    }




    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto) {
        HotelDto created = hotelService.addHotel(hotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable int id, @RequestBody HotelDto hotelDto) {

            HotelDto updated = hotelService.updateHotel(id, hotelDto);
            return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
    }
}
