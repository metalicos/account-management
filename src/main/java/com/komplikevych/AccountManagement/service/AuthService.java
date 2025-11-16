package com.komplikevych.AccountManagement.service;

import com.komplikevych.AccountManagement.dto.request.RegistrationRequest;
import com.komplikevych.AccountManagement.dto.response.AuthResponse;
import com.komplikevych.AccountManagement.dto.response.UserResponse;
import com.komplikevych.AccountManagement.exception.DuplicateEmailException;
import com.komplikevych.AccountManagement.repository.UserRepository;
import com.komplikevych.AccountManagement.security.CustomUserDetailsService;
import com.komplikevych.AccountManagement.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @Transactional
    public UserResponse register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("User with email " + request.email() + " already exists");
        }

        try {
            UserResponse response = userService.createUser(request);
            log.info("User registered successfully with email: {}", request.email());
            return response;
        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations (e.g., unique constraint on email)
            if (e.getMessage() != null && (e.getMessage().contains("email") || e.getMessage().contains("duplicate"))) {
                throw new DuplicateEmailException("User with email " + request.email() + " already exists");
            }
            throw e;
        }
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