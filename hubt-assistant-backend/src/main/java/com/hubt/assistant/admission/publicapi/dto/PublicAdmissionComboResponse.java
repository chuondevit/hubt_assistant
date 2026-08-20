package com.hubt.assistant.admission.publicapi.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PublicAdmissionComboResponse(

        UUID subjectComboId,

        String code,

        String name,

        BigDecimal minimumScore,

        BigDecimal cutoffScore,

        Instant cutoffPublishedAt

) {
}