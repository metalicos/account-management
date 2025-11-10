package com.komplikevych.AccountManagement.mapper;

import com.komplikevych.AccountManagement.dto.response.AddressResponse;
import com.komplikevych.AccountManagement.dto.response.UserResponse;
import com.komplikevych.AccountManagement.model.entity.Address;
import com.komplikevych.AccountManagement.model.entity.User;
import com.komplikevych.AccountManagement.model.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress() != null ? toAddressResponse(user.getAddress()) : null)
                .roles(user.getRoles().stream()
                        .map(UserRole::getRole)
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .country(address.getCountry())
                .state(address.getState())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .building(address.getBuilding())
                .entrance(address.getEntrance())
                .floor(address.getFloor())
                .apartment(address.getApartment())
                .intercomCode(address.getIntercomCode())
                .build();
    }
}

