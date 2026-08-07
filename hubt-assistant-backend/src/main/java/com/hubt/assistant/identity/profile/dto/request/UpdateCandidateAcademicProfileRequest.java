package com.hubt.assistant.identity.profile.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateCandidateAcademicProfileRequest(

        @DecimalMin(
                value = "0.0",
                message = "Điểm Toán phải từ 0 đến 10"
        )
        @DecimalMax(
                value = "10.0",
                message = "Điểm Toán phải từ 0 đến 10"
        )
        BigDecimal mathScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal literatureScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal foreignLanguageScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal naturalScienceScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal socialScienceScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal technologyScore,

        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal averageScore

) {
}