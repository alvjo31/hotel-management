package com.hotel.booking_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;

import java.util.Date;

@Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder

    public class Review {
    @Id
    private Integer id;
    private Double rating;
    private String comment;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
