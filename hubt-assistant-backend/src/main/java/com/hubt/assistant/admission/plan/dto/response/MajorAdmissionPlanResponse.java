package com.hubt.assistant.admission.plan.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MajorAdmissionPlanResponse(

        UUID id,

        UUID admissionYearId,
        Integer admissionYear,

        UUID majorId,
        String majorCode,
        String majorName,

        UUID programId,
        String programCode,
        String programName,

        Integer totalQuota,

        BigDecimal tuitionFee,

        BigDecimal expectedCutoff,

        Boolean applicationOpen,

        String notes,

        OffsetDateTime createdAt,

        OffsetDateTime updatedAt

) {
}