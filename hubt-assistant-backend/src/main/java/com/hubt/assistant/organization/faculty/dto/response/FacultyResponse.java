package com.hubt.assistant.organization.faculty.dto.response;

import java.time.Instant;
import java.util.UUID;


public record FacultyResponse(

        UUID id,

        UUID universityId,

        String universityCode,

        String universityName,

        String code,

        String name,

        String description,

        String deanName,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}