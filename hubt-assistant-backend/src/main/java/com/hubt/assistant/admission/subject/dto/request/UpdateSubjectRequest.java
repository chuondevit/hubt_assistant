package com.hubt.assistant.admission.subject.dto.request;

import jakarta.validation.constraints.Size;


public record UpdateSubjectRequest(

        @Size(
                min = 1,
                max = 30,
                message = "Mã môn học phải từ 1 đến 30 ký tự"
        )
        String code,


        @Size(
                min = 1,
                max = 255,
                message = "Tên môn học phải từ 1 đến 255 ký tự"
        )
        String name

) {
}