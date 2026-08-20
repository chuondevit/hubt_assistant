package com.hubt.assistant.organization.university.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UniversityResponse(

        UUID id,

        String code,

        String name,

        String shortName,

        String description,

        String address,

        String email,

        String phone,

        String website,

        String logoUrl,

        String status,

        Instant createdAt,

        Instant updatedAt

) {
}