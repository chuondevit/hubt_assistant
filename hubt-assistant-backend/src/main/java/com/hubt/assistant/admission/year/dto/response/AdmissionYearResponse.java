package com.hubt.assistant.admission.year.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


public record AdmissionYearResponse(

        UUID id,

        UUID universityId,

        String universityCode,

        String universityName,

        Integer year,

        String name,

        LocalDate startDate,

        LocalDate endDate,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}