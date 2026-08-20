package com.hubt.assistant.admission.subject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateSubjectRequest(

        @NotBlank(
                message = "Mã môn học không được để trống"
        )
        @Size(
                max = 30,
                message = "Mã môn học không được vượt quá 30 ký tự"
        )
        String code,


        @NotBlank(
                message = "Tên môn học không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên môn học không được vượt quá 255 ký tự"
        )
        String name

) {
}