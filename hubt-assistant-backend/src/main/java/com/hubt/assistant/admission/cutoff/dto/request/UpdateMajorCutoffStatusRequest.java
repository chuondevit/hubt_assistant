package com.hubt.assistant.admission.cutoff.dto.request;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateMajorCutoffStatusRequest(

        @NotNull(message = "Trạng thái không được để trống")
        MajorCutoffStatus status

) {
}