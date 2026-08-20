package com.hubt.assistant.admission.method.dto.response;

import java.time.Instant;
import java.util.UUID;


public record AdmissionMethodResponse(

        UUID id,

        UUID universityId,

        String universityCode,

        String universityName,

        String code,

        String name,

        String description,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}