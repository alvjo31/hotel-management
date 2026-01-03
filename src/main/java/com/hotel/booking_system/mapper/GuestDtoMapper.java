package com.hotel.booking_system.mapper;

import com.hotel.booking_system.dto.GuestDto;
import com.hotel.booking_system.model.Guest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Builder
public class GuestDtoMapper implements Function<Guest , GuestDto> {

    public GuestDto apply(Guest guest) {
        return new GuestDto(guest.getId(), guest.getFirstName(),guest.getLastName(),
                guest.getEmail(),guest.getPhone());
    }

    public Guest fromDto(GuestDto guestDto) {
        Guest guest = new Guest();
        guest.setId(guestDto.getId());
        guest.setFirstName(guestDto.getFirstName());
        guest.setLastName(guestDto.getLastName());
        guest.setEmail(guestDto.getEmail());
        guest.setPhone(guestDto.getPhone());
        return guest;
    }
}
