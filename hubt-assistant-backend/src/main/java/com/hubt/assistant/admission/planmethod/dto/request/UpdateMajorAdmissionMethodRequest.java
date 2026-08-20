package com.hubt.assistant.admission.planmethod.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateMajorAdmissionMethodRequest(

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