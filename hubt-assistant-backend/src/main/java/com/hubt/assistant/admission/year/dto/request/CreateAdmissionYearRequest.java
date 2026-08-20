package com.hubt.assistant.admission.year.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;


public record CreateAdmissionYearRequest(

        @NotNull(
                message = "University ID không được để trống"
        )
        UUID universityId,


        @NotNull(
                message = "Năm tuyển sinh không được để trống"
        )
        @Min(
                value = 2000,
                message = "Năm tuyển sinh phải từ 2000"
        )
        @Max(
                value = 2100,
                message = "Năm tuyển sinh không được vượt quá 2100"
        )
        Integer year,


        @Size(
                max = 255,
                message = "Tên kỳ tuyển sinh không được vượt quá 255 ký tự"
        )
        String name,


        LocalDate startDate,

        LocalDate endDate

) {
}