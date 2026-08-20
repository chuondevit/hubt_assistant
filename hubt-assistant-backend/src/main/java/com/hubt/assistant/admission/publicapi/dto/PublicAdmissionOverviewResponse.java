package com.hubt.assistant.admission.publicapi.dto;

import java.util.List;
import java.util.UUID;

public record PublicAdmissionOverviewResponse(

        UUID universityId,

        String universityCode,

        String universityName,

        UUID majorId,

        String majorCode,

        String majorName,

        UUID admissionYearId,

        Integer admissionYear,

        String admissionYearName,

        List<PublicAdmissionPlanResponse> plans

) {
}