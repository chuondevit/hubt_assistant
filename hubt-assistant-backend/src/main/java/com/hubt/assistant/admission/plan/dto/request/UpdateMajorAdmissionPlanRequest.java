package com.hubt.assistant.admission.plan.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMajorAdmissionPlanRequest(

        UUID admissionYearId,

        UUID majorId,

        UUID programId,

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