package com.hubt.assistant.organization.program.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProgramResponse(

        UUID id,

        UUID majorId,
        String majorCode,
        String majorName,

        UUID universityId,
        String universityCode,

        UUID facultyId,
        String facultyCode,

        String code,
        String name,

        String trainingMode,
        String language,

        BigDecimal durationYears,

        String description,

        String status,

        Instant createdAt,
        Instant updatedAt

) {
}