package com.hubt.assistant.admission.method.dto.request;

import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateAdmissionMethodStatusRequest(

        @NotNull(
                message = "Trạng thái phương thức xét tuyển không được để trống"
        )
        AdmissionMethodStatus status

) {
}