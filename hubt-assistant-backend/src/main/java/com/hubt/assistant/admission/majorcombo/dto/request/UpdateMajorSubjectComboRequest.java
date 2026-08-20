package com.hubt.assistant.admission.majorcombo.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateMajorSubjectComboRequest(

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Điểm tối thiểu không được nhỏ hơn 0"
        )
        BigDecimal minimumScore

) {
}