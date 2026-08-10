package com.hubt.assistant.identity.profile.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminCandidateSummaryResponse(

        UUID userId,

        String candidateCode,

        String fullName,

        String email,

        String phone,

        String accountStatus,

        boolean emailVerified,

        boolean phoneVerified,

        BigDecimal profileCompletionPercent,

        Instant createdAt

) {
}