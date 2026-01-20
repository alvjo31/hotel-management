package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.model.Guest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Mapper for converting between Guest entity and GuestDto
 */
@Component
public class GuestDtoMapper implements Function<Guest, GuestDto> {

    @Override
    public GuestDto apply(Guest guest) {
        return GuestDto.builder()
                .guestId(guest.getId())
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                .email(guest.getEmail())
                .phone(guest.getPhone())
                .build();
    }

    public Guest fromDto(GuestDto guestDto) {
        Guest guest = new Guest();
        if (guestDto.getGuestId() != null) {
            guest.setId(guestDto.getGuestId());
        }
        guest.setFirstName(guestDto.getFirstName());
        guest.setLastName(guestDto.getLastName());
        guest.setEmail(guestDto.getEmail());
        guest.setPhone(guestDto.getPhone());
        return guest;
    }
}
