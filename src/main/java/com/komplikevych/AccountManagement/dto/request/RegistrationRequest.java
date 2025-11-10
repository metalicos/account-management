package com.komplikevych.AccountManagement.dto.request;

import com.komplikevych.AccountManagement.model.enums.Gender;
import com.komplikevych.AccountManagement.model.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record RegistrationRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName,
    
    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    String middleName,
    
    Gender gender,
    
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phoneNumber,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    String password,
    
    @Valid
    AddressRequest address,
    
    @NotEmpty(message = "At least one role is required")
    Set<Role> roles
) {
}

