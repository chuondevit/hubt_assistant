package com.hubt.assistant.identity.profile.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CandidateInterestResponse(

        UUID id,

        UUID candidateId,

        String interestCode,

        String interestName,

        String level,

        String source,

        Instant createdAt

) {
}