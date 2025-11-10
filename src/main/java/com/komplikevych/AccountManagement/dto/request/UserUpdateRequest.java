package com.komplikevych.AccountManagement.dto.request;

import com.komplikevych.AccountManagement.model.enums.Gender;
import com.komplikevych.AccountManagement.model.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record UserUpdateRequest(
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName,
    
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName,
    
    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    String middleName,
    
    Gender gender,
    
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phoneNumber,
    
    @Valid
    AddressRequest address,
    
    Set<Role> roles
) {
}

