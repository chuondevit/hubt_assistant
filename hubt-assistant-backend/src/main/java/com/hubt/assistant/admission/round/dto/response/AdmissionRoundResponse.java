package com.hubt.assistant.admission.round.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


public record AdmissionRoundResponse(

        UUID id,

        UUID admissionYearId,

        Integer admissionYear,

        String admissionYearName,

        UUID universityId,

        String universityCode,

        Integer roundNumber,

        String name,

        Instant applicationStartAt,

        Instant applicationEndAt,

        LocalDate resultDate,

        Instant confirmationDeadline,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}