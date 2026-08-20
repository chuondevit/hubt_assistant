package com.hubt.assistant.admission.combination.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateSubjectCombinationRequest(

        @NotBlank(
                message = "Mã tổ hợp không được để trống"
        )
        @Size(
                max = 30,
                message = "Mã tổ hợp không được vượt quá 30 ký tự"
        )
        String code,

        @NotBlank(
                message = "Tên tổ hợp không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên tổ hợp không được vượt quá 255 ký tự"
        )
        String name,

        String description,

        @NotEmpty(
                message = "Tổ hợp phải có ít nhất một môn học"
        )
        List<@Valid SubjectCombinationItemRequest> items

) {
}