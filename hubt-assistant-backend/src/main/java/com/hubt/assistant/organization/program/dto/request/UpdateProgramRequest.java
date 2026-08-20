package com.hubt.assistant.organization.program.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProgramRequest(

        UUID majorId,

        @Size(min = 1, max = 50)
        String code,

        @Size(min = 1, max = 255)
        String name,

        @Size(max = 100)
        String trainingMode,

        @Size(max = 100)
        String language,

        @DecimalMin(value = "0.1")
        BigDecimal durationYears,

        String description

) {
}