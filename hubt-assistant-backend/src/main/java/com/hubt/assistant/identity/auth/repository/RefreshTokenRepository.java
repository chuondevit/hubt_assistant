package com.hubt.assistant.identity.auth.repository;

import com.hubt.assistant.identity.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE RefreshToken rt
            SET rt.revokedAt = :revokedAt
            WHERE rt.user.id = :userId
              AND rt.revokedAt IS NULL
            """)
    int revokeAllByUserId(
            @Param("userId") UUID userId,
            @Param("revokedAt") Instant revokedAt
    );

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.expiresAt < :now
            """)
    int deleteExpiredTokens(@Param("now") Instant now);
}