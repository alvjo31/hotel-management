package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.HotelDto;
import com.hotel.booking_system.exceptions.BadRequestException;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.model.Hotel;
import com.hotel.booking_system.mapper.HotelDtoMapper;
import com.hotel.booking_system.repository.HotelRepository;
import com.hotel.booking_system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFindException("Hotel not found"));
        return hotelDtoMapper.apply(hotel);
    }

    public HotelDto getHotelByName(String hotelName) {

        // if (hotelByName.isPresent()) {
        //   return hotelDtoMapper.apply(hotelByName.get());
        // }else
        //   throw new RuntimeException("Hotel me emrin '" + hotelName + "' nuk u gjet.");

        return hotelRepository.getByHotelName(hotelName)
                .map(hotelDtoMapper)
                .orElseThrow(() -> new ResourceNotFindException(
                        "Hotel me emrin '" + hotelName + "' nuk u gjet."
                ));
    }

@Transactional
    public HotelDto addHotel(HotelDto hotelDto) {
        validationHotelNameDto(hotelDto);
        hotelNameIsUnique(hotelDto.getName());

        Hotel hotel = hotelDtoMapper.fromDto(hotelDto);

        // Vendos automatikisht datat
        hotel.setCreatedDate(LocalDate.now());
        hotel.setUpdatedDate(LocalDate.now());

        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelDtoMapper.apply(hotel);
    }


    // si fillim validon qe emri mos te jete bosh
    private void validationHotelNameDto(HotelDto hotelDto) {
        if (hotelDto.getName() == null || hotelDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Emri i hotelit eshte i detyrueshem" );
        }
        // validon qe qyteti mos te jete bosh
        if (hotelDto.getCity() == null || hotelDto.getCity().trim().isEmpty()) {
            throw new BadRequestException("Qyteti i hotelit eshte i detyrueshem");
        }
    }

    // kontrollon qe emri mos te jete i perseritur
    private void hotelNameIsUnique(String hotelName) {
        hotelRepository.getByHotelName(hotelName)
                .ifPresent(hotel -> {
                    throw new DuplicateResourceException("Hotel me emrin '" + hotelName + "' tashmë ekziston");
                });
    }

@Transactional
    public HotelDto updateHotel(Integer id, HotelDto hotelDto) {
        validationHotelNameDto(hotelDto);
        Hotel existingHotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFindException("Hotel me ID " + id + " nuk ekziston"));
        existingHotel.setHotelCity(hotelDto.getCity());
        existingHotel.setHotelName(hotelDto.getName());
        existingHotel.setHotelAddress(hotelDto.getAddress());
        existingHotel.setHotelDescription(hotelDto.getDescription());
        existingHotel.setHotelEmail(hotelDto.getEmail());

        // Convert String phone to Integer
        if (hotelDto.getPhone() != null && !hotelDto.getPhone().trim().isEmpty()) {
            try {
                String digitsOnly = hotelDto.getPhone().replaceAll("[^0-9]", "");
                existingHotel.setHotelPhone(Integer.parseInt(digitsOnly));
            } catch (NumberFormatException e) {
                throw new BadRequestException("Invalid phone number format: " + hotelDto.getPhone());
            }
        }

        // Note: createdDate and updatedDate are managed by entity @PreUpdate
        return hotelDtoMapper.apply(existingHotel);
    }

    @Transactional
    public void deleteHotel(int id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFindException("Hotel me ID " + id + " nuk ekziston.");
        }
        hotelRepository.deleteById(id);
    }

    public List<HotelDto> getAllHotels() {
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


