package com.hubt.assistant.admission.plan.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateApplicationOpenRequest(

        @NotNull(
                message = "Trạng thái nhận hồ sơ không được để trống"
        )
        Boolean applicationOpen

) {
}