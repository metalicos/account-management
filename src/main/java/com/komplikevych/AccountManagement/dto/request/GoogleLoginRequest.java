package com.komplikevych.AccountManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GoogleLoginRequest(
    @NotBlank(message = "Google ID token is required")
    String idToken
) {
}

