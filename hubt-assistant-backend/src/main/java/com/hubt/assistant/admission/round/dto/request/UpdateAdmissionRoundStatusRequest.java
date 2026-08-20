package com.hubt.assistant.admission.round.dto.request;

import com.hubt.assistant.admission.round.entity.AdmissionRoundStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateAdmissionRoundStatusRequest(

        @NotNull(
                message = "Trạng thái đợt tuyển sinh không được để trống"
        )
        AdmissionRoundStatus status

) {
}