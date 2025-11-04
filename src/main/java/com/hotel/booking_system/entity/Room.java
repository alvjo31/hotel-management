package com.hotel.booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Room {
    @Id
    private String id;
    private Integer number;
    private Integer capacity;
    private Double price;
    private Enum status;
    private int maxGuests;
    @Column(nullable = false)
    private double pricePerNight; // Çmimi për natën për dhomën

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    public int getMaxGuests() {
        return maxGuests;
    }

    public int getPricePerNight(){
        return price.intValue();
    }

    public void setPricePerNight(double pricePerNight){
        this.pricePerNight = pricePerNight;
    }
}
