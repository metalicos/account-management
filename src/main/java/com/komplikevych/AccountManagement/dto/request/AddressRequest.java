package com.komplikevych.AccountManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressRequest(
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    String country,
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    String state,
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    String postalCode,
    
    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    String street,
    
    @NotBlank(message = "Building is required")
    @Size(max = 50, message = "Building must not exceed 50 characters")
    String building,
    
    @Size(max = 10, message = "Entrance must not exceed 10 characters")
    String entrance,
    
    @Size(max = 10, message = "Floor must not exceed 10 characters")
    String floor,
    
    @Size(max = 20, message = "Apartment must not exceed 20 characters")
    String apartment,
    
    @Size(max = 20, message = "Intercom code must not exceed 20 characters")
    String intercomCode
) {
}

