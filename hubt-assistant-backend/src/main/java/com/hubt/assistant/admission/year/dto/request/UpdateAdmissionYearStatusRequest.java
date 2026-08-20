package com.hubt.assistant.admission.year.dto.request;

import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAdmissionYearStatusRequest(

        @NotNull(
                message = "Trạng thái năm tuyển sinh không được để trống"
        )
        AdmissionYearStatus status

) {
}