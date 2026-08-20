package com.hubt.assistant.organization.university.dto.request;

import com.hubt.assistant.organization.university.entity.UniversityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUniversityStatusRequest(

        @NotNull(message = "Trạng thái không được để trống")
        UniversityStatus status

) {
}