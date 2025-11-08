package com.hotel.booking_system.model;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;

    @Column(unique = true ,nullable = false , length = 100)
    private String email;
    private String phone;

    @OneToMany(mappedBy = "guest" , cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();


    @OneToMany(mappedBy = "guest" , cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}
