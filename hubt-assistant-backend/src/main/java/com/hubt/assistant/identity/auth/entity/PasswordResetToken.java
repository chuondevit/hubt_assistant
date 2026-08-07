package com.hubt.assistant.identity.auth.entity;

import com.hubt.assistant.identity.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "password_reset_tokens",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "otp_hash",
            nullable = false,
            length = 64
    )
    private String otpHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(
            name = "reset_token_hash",
            length = 64,
            unique = true
    )
    private String resetTokenHash;

    @Column(name = "reset_token_expires_at")
    private Instant resetTokenExpiresAt;

    @Column(
            name = "failed_attempts",
            nullable = false
    )
    private int failedAttempts = 0;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @Column(
            name = "created_ip",
            length = 64
    )
    private String createdIp;

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isOtpExpired() {
        return expiresAt.isBefore(
                Instant.now()
        );
    }

    public boolean isResetTokenExpired() {

        return resetTokenExpiresAt == null
                || resetTokenExpiresAt.isBefore(
                        Instant.now()
                );
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}