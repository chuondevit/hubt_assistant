package com.hubt.assistant.organization.university.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUniversityRequest(

        @NotBlank(message = "Mã trường không được để trống")
        @Size(max = 50)
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Mã trường chỉ được chứa chữ, số, _ hoặc -"
        )
        String code,

        @NotBlank(message = "Tên trường không được để trống")
        @Size(max = 255)
        String name,

        @Size(max = 100)
        String shortName,

        String description,

        String address,

        @Email(message = "Email không hợp lệ")
        String email,

        @Size(max = 30)
        String phone,

        @Size(max = 255)
        String website,

        String logoUrl

) {
}