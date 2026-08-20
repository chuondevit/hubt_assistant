package com.hubt.assistant.admission.majorcombo.dto.request;

import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectComboStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateMajorSubjectComboStatusRequest(

        @NotNull(message = "Trạng thái không được để trống")
        MajorSubjectComboStatus status

) {
}