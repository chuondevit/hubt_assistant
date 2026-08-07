package com.hubt.assistant.identity.profile.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CandidateAcademicProfileResponse(

        UUID id,

        UUID candidateId,

        Integer version,

        BigDecimal mathScore,

        BigDecimal literatureScore,

        BigDecimal foreignLanguageScore,

        BigDecimal naturalScienceScore,

        BigDecimal socialScienceScore,

        BigDecimal technologyScore,

        BigDecimal averageScore,

        Instant createdAt

) {
}