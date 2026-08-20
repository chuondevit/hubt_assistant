package com.hubt.assistant.admission.publicapi.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PublicAdmissionMethodResponse(

        UUID majorAdmissionMethodId,

        UUID admissionMethodId,

        String code,

        String name,

        String description,

        Integer quota,

        BigDecimal minimumScore,

        Map<String, Object> conditions,

        List<PublicAdmissionComboResponse> combinations

) {
}