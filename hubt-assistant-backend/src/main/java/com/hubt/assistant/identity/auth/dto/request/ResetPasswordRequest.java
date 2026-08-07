package com.hubt.assistant.identity.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Reset token không được để trống")
        String token,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(
                min = 8,
                max = 100,
                message = "Mật khẩu mới phải từ 8 đến 100 ký tự"
        )
        String newPassword,

        @NotBlank(message = "Xác nhận mật khẩu không được để trống")
        String confirmPassword

) {
}