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
    private Integer maxGuests;

    @Column(nullable = false)
    @Builder.Default
    private Double pricePerNight = 0.0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Deprecated: Use maxGuests instead
    @Deprecated
    public int getCapacity() {
        return maxGuests;
    }

    @Deprecated
    public void setCapacity(int capacity) {
        this.maxGuests = capacity;
    }

    // Deprecated: Use pricePerNight instead
    @Deprecated
    public double getPrice() {
        return pricePerNight;
    }

    @Deprecated
    public void setPrice(double price) {
        this.pricePerNight = price;
    }

    public double getPricePerNight() {
        if (pricePerNight == null || pricePerNight < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative.");
        }
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.pricePerNight = pricePerNight;
    }

}
