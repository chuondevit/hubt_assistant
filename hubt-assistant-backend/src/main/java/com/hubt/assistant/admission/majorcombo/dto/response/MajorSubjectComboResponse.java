package com.hubt.assistant.admission.majorcombo.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MajorSubjectComboResponse(

        UUID id,

        UUID majorAdmissionMethodId,

        UUID majorAdmissionPlanId,

        UUID majorId,
        String majorCode,
        String majorName,

        UUID admissionMethodId,
        String admissionMethodCode,
        String admissionMethodName,

        UUID subjectComboId,
        String subjectComboCode,
        String subjectComboName,

        BigDecimal minimumScore,

        String status

) {
}