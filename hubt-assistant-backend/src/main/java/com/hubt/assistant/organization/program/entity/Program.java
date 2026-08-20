package com.hubt.assistant.organization.program.entity;

import com.hubt.assistant.organization.major.entity.Major;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "programs",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "major_id",
            nullable = false
    )
    private Major major;

    @Column(
            name = "code",
            nullable = false,
            length = 50
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @Column(
            name = "training_mode",
            length = 100
    )
    private String trainingMode;

    @Column(
            name = "language",
            length = 100
    )
    private String language;

    @Column(
            name = "duration_years",
            precision = 3,
            scale = 1
    )
    private BigDecimal durationYears;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private ProgramStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {

        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = ProgramStatus.ACTIVE;
        }
    }
}