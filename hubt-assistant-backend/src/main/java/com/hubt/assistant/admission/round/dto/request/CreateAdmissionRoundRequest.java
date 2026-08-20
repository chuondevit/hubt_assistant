package com.hubt.assistant.admission.round.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


public record CreateAdmissionRoundRequest(

        @NotNull(
                message = "Admission Year ID không được để trống"
        )
        UUID admissionYearId,


        @NotNull(
                message = "Số đợt tuyển sinh không được để trống"
        )
        @Min(
                value = 1,
                message = "Số đợt tuyển sinh phải lớn hơn 0"
        )
        Integer roundNumber,


        @NotBlank(
                message = "Tên đợt tuyển sinh không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên đợt tuyển sinh không được vượt quá 255 ký tự"
        )
        String name,


        Instant applicationStartAt,

        Instant applicationEndAt,

        LocalDate resultDate,

        Instant confirmationDeadline

) {
}