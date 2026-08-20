package com.hubt.assistant.admission.cutoff.entity;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;
import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.round.entity.AdmissionRound;
import com.hubt.assistant.admission.year.entity.AdmissionYear;
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
        name = "major_cutoffs",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class MajorCutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "admission_year_id",
            nullable = false
    )
    private AdmissionYear admissionYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_round_id")
    private AdmissionRound admissionRound;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "major_id",
            nullable = false
    )
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "admission_method_id",
            nullable = false
    )
    private AdmissionMethod admissionMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_combo_id")
    private SubjectCombination subjectCombination;

    @Column(
            name = "cutoff_score",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal cutoffScore;

    @Column(name = "published_at")
    private Instant publishedAt;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private MajorCutoffStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (status == null) {
            status = MajorCutoffStatus.ACTIVE;
        }
    }
}