package com.hubt.assistant.identity.auth.dto.response;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String fullName,
        String email,
        List<String> roles,
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}