package com.hubt.assistant.identity.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateCandidateStatusRequest(

        @NotBlank(
                message = "Trạng thái tài khoản không được để trống"
        )
        @Pattern(
                regexp = "^(ACTIVE|INACTIVE|LOCKED|SUSPENDED)$",
                message = "Trạng thái tài khoản không hợp lệ"
        )
        String status

) {
}