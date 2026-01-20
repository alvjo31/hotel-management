package com.hotel.booking_system.service;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.exceptions.DuplicateResourceException;
import com.hotel.booking_system.exceptions.ResourceNotFindException;
import com.hotel.booking_system.mapper.GuestDtoMapper;
import com.hotel.booking_system.model.Guest;
import com.hotel.booking_system.repository.BookingRepository;
import com.hotel.booking_system.repository.GuestRepository;
import jakarta.transaction.Transactional;
import com.hotel.booking_system.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service

public class GuestService {
    private GuestRepository guestRepository;
    private BookingRepository bookingRepository;
    private GuestDtoMapper guestDtoMapper;

    @Autowired
    public GuestService(GuestRepository guestRepository, BookingRepository bookingRepository, GuestDtoMapper guestDtoMapper) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.guestDtoMapper = guestDtoMapper;
    }


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );


    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    @Transactional
    public GuestDto addGuest(GuestDto guestDto) {
        validateGuesDto(guestDto);
        validateGuestDtoForUpdate(guestDto);
        validateUniqueEmail(guestDto.getEmail());
        Guest guest = guestDtoMapper.fromDto(guestDto);
        if (hasActiveBookings(guest)) {
            throw new DuplicateResourceException("Guest " , "status " ,"already active.");
        }
        // e konverton ne entitet
        Guest guestSaved = guestRepository.save(guest);  // save ne db
        return guestDtoMapper.apply(guestSaved);         // kthen dto
    }


    public GuestDto findGuestDtoById(int id) {

        Optional<Guest> guest = guestRepository.findById(id);

        return guestRepository.findById(id)
                .map(guestDtoMapper)
                .orElseThrow(() -> new ResourceNotFindException(
                        "Hotel me emrin '" + guest + "' nuk u gjet."
                ));
    }

    public List<GuestDto> findAllGuests() {

        return guestRepository.findAll()
                .stream()
                .map(guestDtoMapper)
                .toList();

    }

    @Transactional
    public GuestDto updateGuests(Integer id, GuestDto guestDto) {
        validateGuestDtoForUpdate(guestDto);
        Guest existinGguest = guestRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFindException("Guest not found with ID: " + id));
        validateUniqueEmail(guestDto.getEmail());
        if (hasActiveBookings(existinGguest)) {
            throw new DuplicateResourceException("Cannot update guest with active bookings");
        }

        Guest guestSaved = guestRepository.save(existinGguest);
        return guestDtoMapper.apply(guestSaved);
    }

    // Basic validation
    private void validateGuesDto(GuestDto guestDto) {
        if (guestDto.getFirstName() == null || guestDto.getFirstName().isEmpty()) {
            throw new BadRequestException("First name is required");
        }
        if (guestDto.getLastName() == null || guestDto.getLastName().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }
        if (guestDto.getFirstName().length() < 2 || guestDto.getFirstName().length() > 50) {
            throw new BadRequestException(
            "First name must be between 2 and 50 characters");
        }

        if (guestDto.getLastName().length() < 2 || guestDto.getLastName().length() > 50) {
            throw new BadRequestException("Last name must be between 2 and 50 characters");
        }
        if (guestDto.getPhone() == null) {
            throw new BadRequestException("Phone number is required");
        }
        if (guestDto.getEmail().isEmpty() || guestDto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

    }

    // Validation for update

    private void validateGuestDtoForUpdate(GuestDto guestDto) {
        if (guestDto.getLastName() == null || guestDto.getLastName().trim().isEmpty()) {
                throw new BadRequestException("Last name cannot be empty");

        }

        if (guestDto.getEmail() != null && !isValidEmail(guestDto.getEmail())) {
            throw new BadRequestException("Invalid email format");
        }
        if (guestDto.getPhone() != null && !isValidPhone(guestDto.getPhone())) {
            throw new BadRequestException("Invalid phone format");
        }
    }

    private void validateUniqueEmail(String email) {
        if (guestRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Guest with email '" + email + "' already exists");

        }
    }

    // Check if guest has any bookings with status CONFIRMED or CHECKED_IN
    private boolean hasActiveBookings(Guest guest) {
        return guest.getBookings()
                .stream()
                .anyMatch(booking -> booking.getBookingStatus().name().equals("CONFIRMED") ||
                        booking.getBookingStatus().name().equals("CHECKED_IN")

                );

    }

    @Transactional
    public  void deleteRoom(Integer id) {
        if (!guestRepository.existsById(id)) {
            throw new ResourceNotFindException("Room with ID " + id + " not found.");
        }
    }
}
