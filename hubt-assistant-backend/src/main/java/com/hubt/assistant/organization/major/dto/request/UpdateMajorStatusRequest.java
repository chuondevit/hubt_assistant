package com.hubt.assistant.organization.major.dto.request;

import com.hubt.assistant.organization.major.entity.MajorStatus;

import jakarta.validation.constraints.NotNull;


public record UpdateMajorStatusRequest(

        @NotNull(
                message = "Trạng thái ngành không được để trống"
        )
        MajorStatus status

) {
}