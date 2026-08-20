package com.hubt.assistant.admission.publicapi.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PublicAdmissionPlanResponse(

        UUID planId,

        UUID programId,

        String programCode,

        String programName,

        Integer totalQuota,

        BigDecimal tuitionFee,

        BigDecimal expectedCutoff,

        Boolean applicationOpen,

        String notes,

        List<PublicAdmissionMethodResponse> methods

) {
}