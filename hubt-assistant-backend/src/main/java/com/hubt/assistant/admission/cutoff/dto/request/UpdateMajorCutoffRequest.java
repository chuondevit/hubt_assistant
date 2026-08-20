package com.hubt.assistant.admission.cutoff.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateMajorCutoffRequest(

        @DecimalMin(
                value = "0.0",
                message = "Điểm chuẩn không được nhỏ hơn 0"
        )
        BigDecimal cutoffScore,

        Instant publishedAt

) {
}