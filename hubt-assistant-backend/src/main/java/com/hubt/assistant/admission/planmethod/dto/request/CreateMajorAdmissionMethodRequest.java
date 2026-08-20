package com.hubt.assistant.admission.planmethod.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreateMajorAdmissionMethodRequest(

        @NotNull(
                message = "Major Admission Plan ID không được để trống"
        )
        UUID majorAdmissionPlanId,

        @NotNull(
                message = "Admission Method ID không được để trống"
        )
        UUID admissionMethodId,

        @Min(
                value = 0,
                message = "Chỉ tiêu không được nhỏ hơn 0"
        )
        Integer quota,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Điểm tối thiểu không được nhỏ hơn 0"
        )
        BigDecimal minimumScore,

        Map<String, Object> conditionsJson

) {
}