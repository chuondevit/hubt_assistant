package com.hubt.assistant.organization.program.dto.request;

import com.hubt.assistant.organization.program.entity.ProgramStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateProgramStatusRequest(

        @NotNull(
                message = "Trạng thái chương trình không được để trống"
        )
        ProgramStatus status

) {
}