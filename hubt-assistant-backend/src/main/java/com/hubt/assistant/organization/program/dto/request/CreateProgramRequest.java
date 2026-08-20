package com.hubt.assistant.organization.program.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProgramRequest(

        @NotNull(message = "Major ID không được để trống")
        UUID majorId,

        @NotBlank(message = "Mã chương trình không được để trống")
        @Size(
                max = 50,
                message = "Mã chương trình không được vượt quá 50 ký tự"
        )
        String code,

        @NotBlank(message = "Tên chương trình không được để trống")
        @Size(
                max = 255,
                message = "Tên chương trình không được vượt quá 255 ký tự"
        )
        String name,

        @Size(
                max = 100,
                message = "Hình thức đào tạo không được vượt quá 100 ký tự"
        )
        String trainingMode,

        @Size(
                max = 100,
                message = "Ngôn ngữ không được vượt quá 100 ký tự"
        )
        String language,

        @DecimalMin(
                value = "0.1",
                message = "Thời gian đào tạo phải lớn hơn 0"
        )
        BigDecimal durationYears,

        String description

) {
}