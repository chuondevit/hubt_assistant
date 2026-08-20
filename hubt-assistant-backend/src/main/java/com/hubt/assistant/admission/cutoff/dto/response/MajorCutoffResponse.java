package com.hubt.assistant.admission.cutoff.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MajorCutoffResponse(

        UUID id,

        UUID admissionYearId,
        Integer admissionYear,

        UUID admissionRoundId,
        String admissionRoundName,

        UUID majorId,
        String majorCode,
        String majorName,

        UUID admissionMethodId,
        String admissionMethodCode,
        String admissionMethodName,

        UUID subjectComboId,
        String subjectComboCode,
        String subjectComboName,

        BigDecimal cutoffScore,

        Instant publishedAt,

        String status,

        Instant createdAt

) {
}