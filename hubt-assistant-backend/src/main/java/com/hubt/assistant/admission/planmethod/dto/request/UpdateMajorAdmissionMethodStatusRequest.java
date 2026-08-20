package com.hubt.assistant.admission.planmethod.dto.request;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateMajorAdmissionMethodStatusRequest(

        @NotNull(
                message = "Trạng thái không được để trống"
        )
        MajorAdmissionMethodStatus status

) {
}