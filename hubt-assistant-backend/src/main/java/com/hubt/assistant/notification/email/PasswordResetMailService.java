package com.hubt.assistant.notification.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendPasswordResetOtp(
            String recipientEmail,
            String otp
    ) {

        if (senderEmail == null
                || senderEmail.isBlank()) {

            throw new IllegalStateException(
                    "Chưa cấu hình tài khoản gửi email"
            );
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(senderEmail);

        message.setTo(recipientEmail);

        message.setSubject(
                "HUBT Assistant - Mã OTP đặt lại mật khẩu"
        );

        message.setText("""
                Xin chào,

                Bạn vừa yêu cầu đặt lại mật khẩu
                trên hệ thống HUBT Assistant.

                Mã OTP của bạn là:

                %s

                Mã OTP có hiệu lực trong 5 phút.

                Không cung cấp mã OTP này cho bất kỳ ai.

                Nếu bạn không thực hiện yêu cầu này,
                vui lòng bỏ qua email.

                Trân trọng,
                HUBT Assistant
                """.formatted(otp));

        mailSender.send(message);

        log.info(
                "Đã gửi OTP đặt lại mật khẩu tới {}",
                recipientEmail
        );
    }
}