package com.hubt.assistant.organization.major.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record MajorResponse(

        UUID id,

        UUID universityId,

        String universityCode,

        String universityName,

        UUID facultyId,

        String facultyCode,

        String facultyName,

        String code,

        String name,

        String degreeLevel,

        BigDecimal durationYears,

        String description,

        String learningOutcomes,

        String careerOpportunities,

        String requiredSkills,

        String thumbnailUrl,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}