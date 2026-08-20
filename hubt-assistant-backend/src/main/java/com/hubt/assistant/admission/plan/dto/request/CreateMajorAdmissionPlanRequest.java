package com.hubt.assistant.admission.plan.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMajorAdmissionPlanRequest(

        @NotNull(message = "Admission Year ID không được để trống")
        UUID admissionYearId,

        @NotNull(message = "Major ID không được để trống")
        UUID majorId,

        UUID programId,

        @NotNull(message = "Tổng chỉ tiêu không được để trống")
        @Min(
                value = 0,
                message = "Tổng chỉ tiêu không được nhỏ hơn 0"
        )
        Integer totalQuota,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Học phí không được nhỏ hơn 0"
        )
        BigDecimal tuitionFee,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Điểm chuẩn dự kiến không được nhỏ hơn 0"
        )
        BigDecimal expectedCutoff,

        Boolean applicationOpen,

        String notes

) {
}