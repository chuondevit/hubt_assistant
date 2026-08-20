package com.hubt.assistant.organization.university.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUniversityRequest(

        @Size(
                min = 1,
                max = 50,
                message = "Mã trường phải có từ 1 đến 50 ký tự"
        )
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Mã trường chỉ được chứa chữ cái, số, dấu gạch dưới hoặc gạch ngang"
        )
        String code,


        @Size(
                min = 1,
                max = 255,
                message = "Tên trường phải có từ 1 đến 255 ký tự"
        )
        String name,


        @Size(
                max = 100,
                message = "Tên viết tắt không được vượt quá 100 ký tự"
        )
        String shortName,


        String description,


        String address,


        @Email(
                message = "Email không đúng định dạng"
        )
        @Size(
                max = 255,
                message = "Email không được vượt quá 255 ký tự"
        )
        String email,


        @Size(
                max = 30,
                message = "Số điện thoại không được vượt quá 30 ký tự"
        )
        String phone,


        @Size(
                max = 255,
                message = "Website không được vượt quá 255 ký tự"
        )
        String website,


        String logoUrl

) {
}