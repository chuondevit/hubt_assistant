package com.hubt.assistant.organization.faculty.dto.request;

import jakarta.validation.constraints.Size;


public record UpdateFacultyRequest(

        @Size(
                min = 1,
                max = 50,
                message = "Mã khoa phải từ 1 đến 50 ký tự"
        )
        String code,


        @Size(
                min = 1,
                max = 255,
                message = "Tên khoa phải từ 1 đến 255 ký tự"
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