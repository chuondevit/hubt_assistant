package com.hubt.assistant.identity.auth.dto.response;

public record VerifyResetOtpResponse(

        String resetToken,

        long expiresIn

) {
}