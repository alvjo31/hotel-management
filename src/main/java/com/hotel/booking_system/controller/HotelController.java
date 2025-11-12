package com.hotel.booking_system.controller;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
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
       return hotelService.getAllHotels(null);
    }

    @GetMapping("/{id}")
    public HotelDto getHotelById(@PathVariable int id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
    public HotelDto createHotel(@RequestBody HotelDto hotelDto) {
        return hotelService.addHotel(hotelDto);
    }

    @PutMapping("/{id}")
    public HotelDto updateHotel(@PathVariable int id, @RequestBody HotelDto hotelDto) {
        return hotelService.updateHotel(id, hotelDto);
    }
    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
    }
}
