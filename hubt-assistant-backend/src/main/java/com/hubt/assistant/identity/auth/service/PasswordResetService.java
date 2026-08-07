package com.hubt.assistant.identity.auth.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.identity.auth.dto.request.ResetPasswordRequest;
import com.hubt.assistant.identity.auth.entity.PasswordResetToken;
import com.hubt.assistant.identity.auth.repository.PasswordResetTokenRepository;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;
import com.hubt.assistant.notification.email.PasswordResetMailService;
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
    private final PasswordResetTokenRepository tokenRepository;
    private final SecureTokenGenerator secureTokenGenerator;
    private final PasswordResetMailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.auth.password-reset-expiration}")
    private long resetExpiration;

    @Transactional
    public void requestReset(
            String requestEmail,
            String ipAddress
    ) {
        String email = requestEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        userRepository
                .findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .ifPresent(user -> createToken(
                        user,
                        ipAddress
                ));
    }

    private void createToken(
            User user,
            String ipAddress
    ) {
        String rawToken =
                secureTokenGenerator.generateToken();

        PasswordResetToken token =
                new PasswordResetToken();

        token.setUser(user);
        token.setTokenHash(
                secureTokenGenerator.hashToken(rawToken)
        );
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(
                Instant.now().plusMillis(resetExpiration)
        );
        token.setCreatedIp(ipAddress);

        tokenRepository.save(token);

        mailService.sendPasswordResetEmail(
                user.getEmail(),
                rawToken
        );
    }

    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {
        if (!request.newPassword()
                .equals(request.confirmPassword())) {
            throw new BusinessException(
                    "PASSWORD_CONFIRMATION_MISMATCH",
                    "Xác nhận mật khẩu không khớp"
            );
        }

        String tokenHash =
                secureTokenGenerator.hashToken(
                        request.token()
                );

        PasswordResetToken resetToken =
                tokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> new BusinessException(
                                "INVALID_RESET_TOKEN",
                                "Reset token không hợp lệ"
                        ));

        if (resetToken.isUsed()) {
            throw new BusinessException(
                    "RESET_TOKEN_USED",
                    "Reset token đã được sử dụng"
            );
        }

        if (resetToken.isExpired()) {
            throw new BusinessException(
                    "RESET_TOKEN_EXPIRED",
                    "Reset token đã hết hạn"
            );
        }

        User user = resetToken.getUser();

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(user);

        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);

        refreshTokenService.revokeAllByUserId(
                user.getId()
        );
    }
}