package com.hubt.assistant.identity.auth.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.identity.auth.dto.request.ResetPasswordRequest;
import com.hubt.assistant.identity.auth.dto.request.VerifyResetOtpRequest;
import com.hubt.assistant.identity.auth.dto.response.VerifyResetOtpResponse;
import com.hubt.assistant.identity.auth.entity.PasswordResetToken;
import com.hubt.assistant.identity.auth.repository.PasswordResetTokenRepository;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;
import com.hubt.assistant.notification.email.PasswordResetMailService;
import com.hubt.assistant.security.token.OtpGenerator;
import com.hubt.assistant.security.token.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository
            tokenRepository;

    private final SecureTokenGenerator
            secureTokenGenerator;

    private final OtpGenerator otpGenerator;

    private final PasswordResetMailService
            mailService;

    private final PasswordEncoder
            passwordEncoder;

    private final RefreshTokenService
            refreshTokenService;

    @Value(
            "${app.auth.password-reset-otp-expiration:300000}"
    )
    private long otpExpiration;

    @Value(
            "${app.auth.password-reset-session-expiration:600000}"
    )
    private long resetSessionExpiration;

    @Value(
            "${app.auth.password-reset-max-attempts:5}"
    )
    private int maxAttempts;


    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @Transactional
    public void requestReset(
            String requestEmail,
            String ipAddress
    ) {

        String email = requestEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        userRepository
                .findByEmailIgnoreCaseAndDeletedAtIsNull(
                        email
                )
                .ifPresent(
                        user -> createOtp(
                                user,
                                ipAddress
                        )
                );
    }


    private void createOtp(
            User user,
            String ipAddress
    ) {

        Instant now = Instant.now();

        // Hủy OTP/reset token cũ
        tokenRepository.invalidateAllUnusedByUserId(
                user.getId(),
                now
        );

        // Sinh OTP 6 số
        String rawOtp =
                otpGenerator.generateSixDigitOtp();

        PasswordResetToken token =
                new PasswordResetToken();

        token.setUser(user);

        // Không lưu OTP thật trong DB
        token.setOtpHash(
                secureTokenGenerator.hashToken(
                        rawOtp
                )
        );

        token.setExpiresAt(
                now.plusMillis(
                        otpExpiration
                )
        );

        token.setFailedAttempts(0);

        token.setCreatedAt(now);

        token.setCreatedIp(ipAddress);

        tokenRepository.save(token);

        // Chỉ gửi OTP thật qua email
        mailService.sendPasswordResetOtp(
                user.getEmail(),
                rawOtp
        );
    }


    // =========================================================
    // VERIFY OTP
    // =========================================================

    @Transactional
    public VerifyResetOtpResponse verifyOtp(
            VerifyResetOtpRequest request
    ) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCaseAndDeletedAtIsNull(
                        email
                )
                .orElseThrow(
                        () -> new BusinessException(
                                "RESET_OTP_INVALID",
                                "OTP không hợp lệ"
                        )
                );

        PasswordResetToken token =
                tokenRepository
                        .findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(
                                user.getId()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "RESET_OTP_INVALID",
                                        "OTP không hợp lệ"
                                )
                        );

        if (token.isUsed()) {

            throw new BusinessException(
                    "RESET_OTP_USED",
                    "OTP đã được sử dụng"
            );
        }

        if (token.isOtpExpired()) {

            throw new BusinessException(
                    "RESET_OTP_EXPIRED",
                    "OTP đã hết hạn"
            );
        }

        if (token.getFailedAttempts()
                >= maxAttempts) {

            throw new BusinessException(
                    "RESET_OTP_ATTEMPTS_EXCEEDED",
                    "Bạn đã nhập sai OTP quá nhiều lần"
            );
        }

        String requestOtpHash =
                secureTokenGenerator.hashToken(
                        request.otp()
                );

        if (!requestOtpHash.equals(
                token.getOtpHash()
        )) {

            token.setFailedAttempts(
                    token.getFailedAttempts() + 1
            );

            tokenRepository.save(token);

            throw new BusinessException(
                    "RESET_OTP_INVALID",
                    "OTP không chính xác"
            );
        }

        Instant now = Instant.now();

        token.setVerifiedAt(now);

        // Sinh reset token mạnh sau khi OTP đúng
        String rawResetToken =
                secureTokenGenerator.generateToken();

        token.setResetTokenHash(
                secureTokenGenerator.hashToken(
                        rawResetToken
                )
        );

        token.setResetTokenExpiresAt(
                now.plusMillis(
                        resetSessionExpiration
                )
        );

        tokenRepository.save(token);

        return new VerifyResetOtpResponse(
                rawResetToken,
                resetSessionExpiration / 1000
        );
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        if (!request.newPassword()
                .equals(
                        request.confirmPassword()
                )) {

            throw new BusinessException(
                    "PASSWORD_CONFIRMATION_MISMATCH",
                    "Xác nhận mật khẩu không khớp"
            );
        }

        String resetTokenHash =
                secureTokenGenerator.hashToken(
                        request.token()
                );

        PasswordResetToken resetToken =
                tokenRepository
                        .findByResetTokenHashAndUsedAtIsNull(
                                resetTokenHash
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "INVALID_RESET_TOKEN",
                                        "Reset token không hợp lệ"
                                )
                        );

        if (!resetToken.isVerified()) {

            throw new BusinessException(
                    "RESET_OTP_NOT_VERIFIED",
                    "OTP chưa được xác thực"
            );
        }

        if (resetToken.isResetTokenExpired()) {

            throw new BusinessException(
                    "RESET_TOKEN_EXPIRED",
                    "Phiên đặt lại mật khẩu đã hết hạn"
            );
        }

        if (resetToken.isUsed()) {

            throw new BusinessException(
                    "RESET_TOKEN_USED",
                    "Reset token đã được sử dụng"
            );
        }

        User user =
                resetToken.getUser();

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {

            throw new BusinessException(
                    "PASSWORD_NOT_CHANGED",
                    "Mật khẩu mới phải khác mật khẩu hiện tại"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(user);

        resetToken.setUsedAt(
                Instant.now()
        );

        tokenRepository.save(
                resetToken
        );

        // Bắt đăng nhập lại toàn bộ thiết bị
        refreshTokenService
                .revokeAllByUserId(
                        user.getId()
                );
    }
}