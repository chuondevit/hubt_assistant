package com.hubt.assistant.admission.majorcombo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMajorSubjectComboRequest(

        @NotNull(
                message = "Major Admission Method ID không được để trống"
        )
        UUID majorAdmissionMethodId,

        @NotNull(
                message = "Subject Combination ID không được để trống"
        )
        UUID subjectComboId,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Điểm tối thiểu không được nhỏ hơn 0"
        )
        BigDecimal minimumScore

) {
}