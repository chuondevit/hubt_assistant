package com.hubt.assistant.organization.major.entity;

import com.hubt.assistant.organization.faculty.entity.Faculty;
import com.hubt.assistant.organization.university.entity.University;

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
        name = "majors",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID id;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "university_id",
            nullable = false
    )
    private University university;


    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "faculty_id"
    )
    private Faculty faculty;


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


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "degree_level",
            nullable = false,
            columnDefinition = "hubt.degree_level"
    )
    private DegreeLevel degreeLevel;


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


    @Column(
            name = "learning_outcomes",
            columnDefinition = "TEXT"
    )
    private String learningOutcomes;


    @Column(
            name = "career_opportunities",
            columnDefinition = "TEXT"
    )
    private String careerOpportunities;


    @Column(
            name = "required_skills",
            columnDefinition = "TEXT"
    )
    private String requiredSkills;


    @Column(
            name = "thumbnail_url",
            columnDefinition = "TEXT"
    )
    private String thumbnailUrl;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private MajorStatus status;


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


    @Column(
            name = "deleted_at"
    )
    private Instant deletedAt;


    @PrePersist
    protected void onCreate() {

        Instant now =
                Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (degreeLevel == null) {
            degreeLevel =
                    DegreeLevel.BACHELOR;
        }

        if (status == null) {
            status =
                    MajorStatus.ACTIVE;
        }
    }
}