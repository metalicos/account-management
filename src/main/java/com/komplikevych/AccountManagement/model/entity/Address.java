package com.komplikevych.AccountManagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @Column(name = "building", nullable = false, length = 50)
    private String building;

    @Column(name = "entrance", length = 10)
    private String entrance;

    @Column(name = "floor", length = 10)
    private String floor;

    @Column(name = "apartment", length = 20)
    private String apartment;

    @Column(name = "intercom_code", length = 20)
    private String intercomCode;
}

