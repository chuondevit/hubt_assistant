package com.hubt.assistant.identity.profile.entity;

import com.hubt.assistant.identity.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "candidate_academic_profiles",
        schema = "hubt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_candidate_academic_version",
                        columnNames = {
                                "candidate_id",
                                "version"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CandidateAcademicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "candidate_id",
            nullable = false
    )
    private User candidate;

    @Column(
            name = "version",
            nullable = false
    )
    private Integer version;

    @Column(
            name = "math_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal mathScore;

    @Column(
            name = "literature_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal literatureScore;

    @Column(
            name = "foreign_language_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal foreignLanguageScore;

    @Column(
            name = "natural_science_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal naturalScienceScore;

    @Column(
            name = "social_science_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal socialScienceScore;

    @Column(
            name = "technology_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal technologyScore;

    @Column(
            name = "average_score",
            precision = 4,
            scale = 2
    )
    private BigDecimal averageScore;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (version == null) {
            version = 1;
        }
    }
}