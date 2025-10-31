package com.hotel.booking_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Guest {
    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer phone;

    @OneToMany(mappedBy = "booking" , cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();


    @OneToMany(mappedBy = "review" , cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}
