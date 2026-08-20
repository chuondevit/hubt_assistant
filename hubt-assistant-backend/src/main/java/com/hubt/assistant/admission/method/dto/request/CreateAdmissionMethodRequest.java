package com.hubt.assistant.admission.method.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public record CreateAdmissionMethodRequest(

        @NotNull(
                message = "University ID không được để trống"
        )
        UUID universityId,


        @NotBlank(
                message = "Mã phương thức xét tuyển không được để trống"
        )
        @Size(
                max = 50,
                message = "Mã phương thức xét tuyển không được vượt quá 50 ký tự"
        )
        String code,


        @NotBlank(
                message = "Tên phương thức xét tuyển không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên phương thức xét tuyển không được vượt quá 255 ký tự"
        )
        String name,


        String description

) {
}