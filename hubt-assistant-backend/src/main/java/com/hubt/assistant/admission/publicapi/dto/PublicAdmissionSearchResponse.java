package com.hubt.assistant.admission.publicapi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicAdmissionSearchResponse(

        UUID planId,

        UUID universityId,
        String universityCode,
        String universityName,

        UUID majorId,
        String majorCode,
        String majorName,

        UUID programId,
        String programCode,
        String programName,

        UUID admissionYearId,
        Integer admissionYear,

        Integer totalQuota,

        BigDecimal tuitionFee,

        BigDecimal expectedCutoff,

        Boolean applicationOpen

) {
}