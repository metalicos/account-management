package com.komplikevych.AccountManagement.service;

import com.komplikevych.AccountManagement.dto.request.RegistrationRequest;
import com.komplikevych.AccountManagement.dto.response.AuthResponse;
import com.komplikevych.AccountManagement.dto.response.UserResponse;
import com.komplikevych.AccountManagement.mapper.UserMapper;
import com.komplikevych.AccountManagement.model.entity.Address;
import com.komplikevych.AccountManagement.model.entity.User;
import com.komplikevych.AccountManagement.model.entity.UserRole;
import com.komplikevych.AccountManagement.repository.UserRepository;
import com.komplikevych.AccountManagement.security.CustomUserDetailsService;
import com.komplikevych.AccountManagement.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }

        Address address = Address.builder()
                .country(request.address().country())
                .state(request.address().state())
                .city(request.address().city())
                .postalCode(request.address().postalCode())
                .street(request.address().street())
                .building(request.address().building())
                .entrance(request.address().entrance())
                .floor(request.address().floor())
                .apartment(request.address().apartment())
                .intercomCode(request.address().intercomCode())
                .build();

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .gender(request.gender())
                .dateOfBirth(request.dateOfBirth())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .address(address)
                .build();

        Set<UserRole> userRoles = request.roles().stream()
                .map(role -> UserRole.builder()
                        .user(user)
                        .role(role)
                        .build())
                .collect(Collectors.toSet());

        user.setRoles(userRoles);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with email: {}", request.email());
        return userMapper.toResponse(savedUser);
    }

    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        log.info("User logged in successfully: {}", email);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        log.info("Token refreshed for user: {}", email);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }
}

