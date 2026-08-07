package com.hubt.assistant.identity.auth.repository;

import com.hubt.assistant.identity.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken>
    findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(
            UUID userId
    );

    Optional<PasswordResetToken>
    findByResetTokenHashAndUsedAtIsNull(
            String resetTokenHash
    );

    @Modifying
    @Query("""
            UPDATE PasswordResetToken token
            SET token.usedAt = :now
            WHERE token.user.id = :userId
              AND token.usedAt IS NULL
            """)
    int invalidateAllUnusedByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now
    );
}