package com.hotel.booking_system.model;

import com.hotel.booking_system.enums.BookingStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;
    @Column(nullable = false)
    private Integer number;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Double price = 0.0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // ose STRING
    private BookingStatus bookingStatus = BookingStatus.PENDING;
    @Column(nullable = false)
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


    //Nëse atributi price është një Double, duhet të jeni të sigurt se price.intValue() është përdorur saktë. Nëse ndonjëherë price është null
    // , mund të shkaktoni një NullPointerException. Një mundësi është që të kontrolloni që price nuk është null:
    public double getPricePerNight(){
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price cannot be null.");
        }
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight){
        this.pricePerNight = pricePerNight;
    }


}
