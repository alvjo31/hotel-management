package com.hotel.booking_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, length = 50)
    private String hotelName;
    @Column(nullable = false, length = 50)
    private String hotelAddress;
    @Column(nullable = false, length = 50)
    private String hotelCity;
    @Column(nullable = false, length = 50)
    private String hotelDescription;
    @Column(nullable = false, length = 50)
    private Integer hotelPhone;
    @Column(nullable = false, length = 50, unique = true)
    private String hotelEmail;
    @Column(name = "created_at ", nullable = false, updatable = false)
    private LocalDate createdDate;
    @Column(name = "updated_at")
    private LocalDate updatedDate;

    private Integer number;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();


}
