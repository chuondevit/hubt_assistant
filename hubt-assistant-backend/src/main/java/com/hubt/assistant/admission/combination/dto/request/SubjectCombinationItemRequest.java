package com.hubt.assistant.admission.combination.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record SubjectCombinationItemRequest(

        @NotNull(
                message = "Subject ID không được để trống"
        )
        UUID subjectId,

        @NotNull(
                message = "Hệ số môn học không được để trống"
        )
        @DecimalMin(
                value = "0.01",
                message = "Hệ số phải lớn hơn 0"
        )
        BigDecimal coefficient

) {
}