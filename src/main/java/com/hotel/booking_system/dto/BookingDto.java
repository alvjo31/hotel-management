package com.hotel.booking_system.dto;

import com.hotel.booking_system.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    @NotNull(message = "Room id is required")
    private Integer roomId;//  INPUT: Cilin dhomë dëshiro
    @NotNull(message = "Guest id is required")
    private Integer guestId;  //  INPUT: Kush je?
    @NotNull(message = "Check in is required")
    @Future (message = "Check-in date must be in the future")
    private LocalDate checkInDate; // INPUT: Kur hyn?
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate; // INPUT: Kur del?
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 10, message = "Maximum 10 guests allowed")
    private Integer numberOfGuests; // INPUT: Sa persona?



    // RESPONSE only (nuk i dërgon user-i):
    private double price;   // OUTPUT: "100 euro "
    private BookingStatus status;    //  OUTPUT: PENDING
    private String hotelName; // OUTPUT: "Grand Hotel"
    private Integer numberOfNights;  // OUTPUT: 5 nights
    private String guestFirstName;  // OUTPUT: "John "
    private String guestLastName;  // OUTPUT: " Doe"


}
