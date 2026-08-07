package com.hubt.assistant.identity.auth.dto.response;

import java.util.List;
import java.util.UUID;

public record CurrentUserResponse(
        UUID id,
        String fullName,
        String email,
        String universityCode,
        String accountStatus,
        List<String> roles,
        List<String> permissions
) {
}