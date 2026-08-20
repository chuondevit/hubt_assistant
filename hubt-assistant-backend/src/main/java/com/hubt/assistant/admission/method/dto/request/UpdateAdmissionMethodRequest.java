package com.hubt.assistant.admission.method.dto.request;

import jakarta.validation.constraints.Size;


public record UpdateAdmissionMethodRequest(

        @Size(
                min = 1,
                max = 50
        )
        String code,


        @Size(
                min = 1,
                max = 255
        )
        String name,


        String description

) {
}