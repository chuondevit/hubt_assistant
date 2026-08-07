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

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendPasswordResetEmail(
            String recipientEmail,
            String rawToken
    ) {
        String resetLink =
                resetPasswordUrl + "?token=" + rawToken;

        log.info(
                "Password reset link for {}: {}",
                recipientEmail,
                resetLink
        );

        /*
         * Trong môi trường phát triển, nếu chưa cấu hình email
         * thì chỉ in đường dẫn reset ra terminal.
         */
        if (senderEmail == null || senderEmail.isBlank()) {
            log.warn(
                    "Chưa cấu hình tài khoản gửi email. "
                            + "Reset link chỉ được ghi ra terminal."
            );
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(
                "HUBT Assistant - Đặt lại mật khẩu"
        );
        message.setText(
                """
                Bạn đã yêu cầu đặt lại mật khẩu HUBT Assistant.

                Truy cập liên kết sau:
                %s

                Liên kết này sẽ hết hạn sau 15 phút.

                Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.
                """.formatted(resetLink)
        );

        mailSender.send(message);
    }
}