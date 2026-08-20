package com.hubt.assistant.organization.faculty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public record CreateFacultyRequest(

        @NotNull(
                message = "University ID không được để trống"
        )
        UUID universityId,


        @NotBlank(
                message = "Mã khoa không được để trống"
        )
        @Size(
                max = 50,
                message = "Mã khoa không được vượt quá 50 ký tự"
        )
        String code,


        @NotBlank(
                message = "Tên khoa không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên khoa không được vượt quá 255 ký tự"
        )
        String name,


        String description,


        @Size(
                max = 255,
                message = "Tên trưởng khoa không được vượt quá 255 ký tự"
        )
        String deanName

) {
}