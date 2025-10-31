package com.hotel.booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
   private Date checkInDate;
   private Date checkOutDate;
   private double price;
   private Enum status;

   @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

   @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;
}
