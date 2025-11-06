package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.mapper.HotelDtoMapper;
import com.hotel.booking_system.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelDtoMapper hotelDtoMapper;

    @Autowired
    public HotelService(HotelRepository hotelRepository, HotelDtoMapper hotelDtoMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelDtoMapper = hotelDtoMapper;
    }


    public HotelDto getHotelById(int id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotelDtoMapper.apply(hotel.get());
    }

    public HotelDto getHotelByName(String hotelName) {
        Optional<Hotel> hotelByName = hotelRepository.getByHotelName(hotelName);

        // if (hotelByName.isPresent()) {
        //   return hotelDtoMapper.apply(hotelByName.get());
        // }else
        //   throw new RuntimeException("Hotel me emrin '" + hotelName + "' nuk u gjet.");

        return hotelRepository.getByHotelName(hotelName)
                .map(hotelDtoMapper)
                .orElseThrow(() -> new RuntimeException(
                        "Hotel me emrin '" + hotelName + "' nuk u gjet."
                ));
    }


    public HotelDto addHotel(HotelDto hotelDto) {
        validationHotelNameDto(hotelDto);
        hotelNameIsUnique(hotelDto.getHotelName());

        Hotel hotel = hotelDtoMapper.fromDto(hotelDto);
        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelDtoMapper.apply(hotel);
    }


    // si fillim validon qe emri mos te jete bosh
    public void validationHotelNameDto(HotelDto hotelDto) {
        if (hotelDto.getHotelName() == null || hotelDto.getHotelName().trim().isEmpty()) {
            throw new RuntimeException("Emri i hotelit eshte i detyrueshem");
        }
        // validon qe qyteti mos te jete bosh
        if (hotelDto.getHotelCity() == null || hotelDto.getHotelCity().trim().isEmpty()) {
            throw new RuntimeException("Qyteti i hotelit eshte i detyrueshem");
        }
    }

    // kontrollon qe emri mos te jete i perseritur
    public void hotelNameIsUnique(String hotelName) {
        hotelRepository.getByHotelName(hotelName)
                .ifPresent(hotel -> {
                    throw new RuntimeException("Hotel me emrin '" + hotelName + "' tashmë ekziston");
                });
    }


    public HotelDto updateHotel(HotelDto hotelDto) {
        validationHotelNameDto(hotelDto);

        Hotel updatedHotel = hotelDtoMapper.fromDto(hotelDto);
        Hotel updateHotel = hotelRepository.save(updatedHotel);
        return hotelDtoMapper.apply(updateHotel);
    }

    public void deleteHotel(int id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel me ID " + id + " nuk ekziston.");
        }
        hotelRepository.deleteById(id);
    }

    public List<HotelDto> getAllHotels(String hotelName) {
        return hotelRepository.findAll()
                .stream()
                .map(hotelDtoMapper)
                .toList();
    }

    public List<HotelDto> getHotelsByCity(String hotelCity) {
        return hotelRepository.getByHotelCity(hotelCity)
                .stream()
                .map(hotelDtoMapper)
                .toList();
    }

}


