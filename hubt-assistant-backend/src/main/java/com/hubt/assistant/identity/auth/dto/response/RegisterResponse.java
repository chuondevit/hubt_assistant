package com.hubt.assistant.identity.auth.dto.response;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String fullName,
        String email,
        String role,
        String status
) {
}