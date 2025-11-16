package com.komplikevych.AccountManagement.service;

import com.komplikevych.AccountManagement.dto.request.AddressRequest;
import com.komplikevych.AccountManagement.dto.request.RegistrationRequest;
import com.komplikevych.AccountManagement.dto.request.UserUpdateRequest;
import com.komplikevych.AccountManagement.dto.response.UserResponse;
import com.komplikevych.AccountManagement.exception.ResourceNotFoundException;
import com.komplikevych.AccountManagement.mapper.UserMapper;
import com.komplikevych.AccountManagement.model.entity.Address;
import com.komplikevych.AccountManagement.model.entity.User;
import com.komplikevych.AccountManagement.model.entity.UserRole;
import com.komplikevych.AccountManagement.model.enums.Role;
import com.komplikevych.AccountManagement.repository.UserRepository;
import com.komplikevych.AccountManagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(RegistrationRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .gender(request.gender())
                .dateOfBirth(request.dateOfBirth())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        Set<UserRole> userRoles = Set.of(Role.USER_COMMUNITY).stream()
                .map(role -> UserRole.builder().user(user).role(role).build())
                .collect(Collectors.toSet());

        user.setRoles(userRoles);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public UserResponse getUserById(@NonNull Long id) {
        log.debug("Fetching user from database with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        log.debug("Fetching current user from database with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Transactional
    @CacheEvict(value = {"users"}, allEntries = true)
    public UserResponse updateCurrentUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        updateUserFields(user, request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"users"}, key = "#id")
    public UserResponse updateUser(@NonNull Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        updateUserFields(user, request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @CacheEvict(value = {"users"}, key = "#id")
    public void deleteUser(@NonNull Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.middleName() != null) user.setMiddleName(request.middleName());
        if (request.gender() != null) user.setGender(request.gender());
        if (request.dateOfBirth() != null) user.setDateOfBirth(request.dateOfBirth());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.address() != null) updateAddress(user, request.address());
        if (request.roles() != null && !request.roles().isEmpty()) updateRoles(user, request.roles());
    }

    private void updateAddress(User user, AddressRequest addressRequest) {
        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }
        if (addressRequest.country() != null) address.setCountry(addressRequest.country());
        if (addressRequest.state() != null) address.setState(addressRequest.state());
        if (addressRequest.city() != null) address.setCity(addressRequest.city());
        if (addressRequest.postalCode() != null) address.setPostalCode(addressRequest.postalCode());
        if (addressRequest.street() != null) address.setStreet(addressRequest.street());
        if (addressRequest.building() != null) address.setBuilding(addressRequest.building());
        if (addressRequest.entrance() != null) address.setEntrance(addressRequest.entrance());
        if (addressRequest.floor() != null) address.setFloor(addressRequest.floor());
        if (addressRequest.apartment() != null) address.setApartment(addressRequest.apartment());
        if (addressRequest.intercomCode() != null) address.setIntercomCode(addressRequest.intercomCode());
    }

    private void updateRoles(User user, Set<Role> roles) {
        userRoleRepository.deleteUserRoleByUser_Id(user.getId());
        user.getRoles().clear();

        Set<UserRole> userRoles = roles.stream()
                .map(role -> UserRole.builder().user(user).role(role).build())
                .collect(Collectors.toSet());

        user.getRoles().addAll(userRoles);
    }
}