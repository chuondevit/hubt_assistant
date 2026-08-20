package com.hubt.assistant.admission.planmethod.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record MajorAdmissionMethodResponse(

        UUID id,

        UUID majorAdmissionPlanId,

        UUID admissionYearId,

        Integer admissionYear,

        UUID majorId,

        String majorCode,

        String majorName,

        UUID admissionMethodId,

        String admissionMethodCode,

        String admissionMethodName,

        Integer quota,

        BigDecimal minimumScore,

        Map<String, Object> conditionsJson,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}