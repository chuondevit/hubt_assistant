package com.hubt.assistant.identity.profile.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CandidateProfileResponse(

        UUID userId,

        String candidateCode,

        String fullName,

        String email,

        String phone,

        LocalDate dateOfBirth,

        String gender,

        String avatarUrl,

        String accountStatus,

        boolean emailVerified,

        boolean phoneVerified,

        String identityNumber,

        String schoolName,

        String provinceCode,

        String districtCode,

        Integer graduationYear,

        String educationLevel,

        String careerGoal,

        String preferredStudyLocation,

        BigDecimal profileCompletionPercent,

        boolean profileCompleted,

        List<String> missingFields,

        Instant createdAt,

        Instant updatedAt

) {
}