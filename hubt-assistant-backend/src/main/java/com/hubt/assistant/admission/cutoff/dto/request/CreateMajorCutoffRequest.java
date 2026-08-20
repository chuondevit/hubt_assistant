package com.hubt.assistant.admission.cutoff.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateMajorCutoffRequest(

        @NotNull(message = "Admission Year ID không được để trống")
        UUID admissionYearId,

        UUID admissionRoundId,

        @NotNull(message = "Major ID không được để trống")
        UUID majorId,

        @NotNull(message = "Admission Method ID không được để trống")
        UUID admissionMethodId,

        UUID subjectComboId,

        @NotNull(message = "Điểm chuẩn không được để trống")
        @DecimalMin(
                value = "0.0",
                message = "Điểm chuẩn không được nhỏ hơn 0"
        )
        BigDecimal cutoffScore,

        Instant publishedAt

) {
}