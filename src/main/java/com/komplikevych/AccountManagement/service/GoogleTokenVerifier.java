package com.komplikevych.AccountManagement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class GoogleTokenVerifier {
    private final String clientId;
    private final ObjectMapper objectMapper;

    public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
        this.clientId = clientId;
        this.objectMapper = new ObjectMapper();

        if (clientId == null || clientId.isEmpty() || clientId.equals("YOUR_GOOGLE_CLIENT_ID")) {
            log.warn("Google Client ID is not configured! Google OAuth will not work properly.");
        }
    }

    public TokenPayload verifyToken(String idTokenString) {
        try {
            if (idTokenString == null || idTokenString.isEmpty()) {
                throw new IllegalArgumentException("ID token is empty");
            }

            String[] parts = idTokenString.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payload = objectMapper.readTree(payloadJson);
            if (clientId != null && !clientId.isEmpty() && !clientId.equals("YOUR_GOOGLE_CLIENT_ID")) {
                String audience = payload.get("aud").asText();
                if (!clientId.equals(audience)) {
                    throw new IllegalArgumentException("Token audience does not match client ID. Expected: " + clientId + ", Got: " + audience);
                }
            } else {
                log.warn("Skipping client ID verification - Google Client ID is not configured");
            }
            long exp = payload.get("exp").asLong();
            if (System.currentTimeMillis() / 1000 > exp) throw new IllegalArgumentException("Token has expired");
            String email = payload.has("email") ? payload.get("email").asText() : null;
            String firstName = payload.has("given_name") ? payload.get("given_name").asText() : null;
            String lastName = payload.has("family_name") ? payload.get("family_name").asText() : null;
            String picture = payload.has("picture") ? payload.get("picture").asText() : null;

            if (email == null) {
                throw new IllegalArgumentException("Email not found in token");
            }

            return new TokenPayload(email, firstName, lastName, picture);
        } catch (Exception e) {
            log.error("Error verifying Google ID token", e);
            throw new IllegalArgumentException("Failed to verify Google ID token: " + e.getMessage());
        }
    }

    public record TokenPayload(String email, String firstName, String lastName, String picture) {
    }
}

