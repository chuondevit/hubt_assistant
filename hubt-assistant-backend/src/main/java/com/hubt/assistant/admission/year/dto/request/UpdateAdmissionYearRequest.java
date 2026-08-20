package com.hubt.assistant.admission.year.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record UpdateAdmissionYearRequest(

        @Min(2000)
        @Max(2100)
        Integer year,


        @Size(max = 255)
        String name,


        LocalDate startDate,

        LocalDate endDate

) {
}