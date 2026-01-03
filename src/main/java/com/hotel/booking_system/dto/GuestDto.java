package com.hotel.booking_system.dto;

import com.hotel.booking_system.model.Guest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestDto {
    @NotNull(message = "idja nuk duhet te jete null")
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
