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
        name = "candidate_profiles",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(
            name = "candidate_code",
            length = 50,
            unique = true
    )
    private String candidateCode;

    @Column(
            name = "identity_number",
            length = 50,
            unique = true
    )
    private String identityNumber;

    @Column(
            name = "school_name",
            length = 255
    )
    private String schoolName;

    @Column(
            name = "province_code",
            length = 30
    )
    private String provinceCode;

    @Column(
            name = "district_code",
            length = 30
    )
    private String districtCode;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(
            name = "education_level",
            length = 100
    )
    private String educationLevel;

    @Column(
            name = "career_goal",
            columnDefinition = "TEXT"
    )
    private String careerGoal;

    @Column(
            name = "preferred_study_location",
            length = 255
    )
    private String preferredStudyLocation;

    @Column(
            name = "profile_completion_percent",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal profileCompletionPercent =
            BigDecimal.ZERO;

    @Column(
            name = "created_at",
            nullable = false
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

        if (profileCompletionPercent == null) {
            profileCompletionPercent =
                    BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}