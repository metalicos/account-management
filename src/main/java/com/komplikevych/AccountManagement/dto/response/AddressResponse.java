package com.komplikevych.AccountManagement.dto.response;

import lombok.Builder;

@Builder
public record AddressResponse(
    Long id,
    String country,
    String state,
    String city,
    String postalCode,
    String street,
    String building,
    String entrance,
    String floor,
    String apartment,
    String intercomCode
) {
}

