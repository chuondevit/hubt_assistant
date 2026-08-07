package com.hubt.assistant.identity.auth.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.identity.auth.entity.RefreshToken;
import com.hubt.assistant.identity.auth.repository.RefreshTokenRepository;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.security.jwt.JwtProperties;
import com.hubt.assistant.security.token.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureTokenGenerator secureTokenGenerator;
    private final JwtProperties jwtProperties;

    @Transactional
    public String create(
            User user,
            String ipAddress,
            String userAgent
    ) {
        String rawToken = secureTokenGenerator.generateToken();
        String tokenHash =
                secureTokenGenerator.hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(
                        jwtProperties.getRefreshTokenExpiration()
                )
        );
        refreshToken.setCreatedIp(ipAddress);
        refreshToken.setUserAgent(userAgent);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String rawToken) {
        String tokenHash =
                secureTokenGenerator.hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(
                        "INVALID_REFRESH_TOKEN",
                        "Refresh token không hợp lệ"
                ));

        if (refreshToken.isRevoked()) {
            throw new BusinessException(
                    "REFRESH_TOKEN_REVOKED",
                    "Refresh token đã bị thu hồi"
            );
        }

        if (refreshToken.isExpired()) {
            throw new BusinessException(
                    "REFRESH_TOKEN_EXPIRED",
                    "Refresh token đã hết hạn"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(String rawToken) {
        String tokenHash =
                secureTokenGenerator.hashToken(rawToken);

        refreshTokenRepository
                .findByTokenHash(tokenHash)
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> {
                    token.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional
    public void revokeAllByUserId(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(
                userId,
                Instant.now()
        );
    }
}