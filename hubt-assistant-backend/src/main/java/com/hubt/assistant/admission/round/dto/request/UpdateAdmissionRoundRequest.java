package com.hubt.assistant.admission.round.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;


public record UpdateAdmissionRoundRequest(

        @Min(
                value = 1,
                message = "Số đợt tuyển sinh phải lớn hơn 0"
        )
        Integer roundNumber,


        @Size(
                min = 1,
                max = 255,
                message = "Tên đợt tuyển sinh phải từ 1 đến 255 ký tự"
        )
        String name,


        Instant applicationStartAt,

        Instant applicationEndAt,

        LocalDate resultDate,

        Instant confirmationDeadline

) {
}