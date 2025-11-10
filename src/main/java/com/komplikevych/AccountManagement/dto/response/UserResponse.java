package com.komplikevych.AccountManagement.dto.response;

import com.komplikevych.AccountManagement.model.enums.Gender;
import com.komplikevych.AccountManagement.model.enums.Role;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String middleName,
    Gender gender,
    LocalDate dateOfBirth,
    String email,
    String phoneNumber,
    AddressResponse address,
    Set<Role> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

