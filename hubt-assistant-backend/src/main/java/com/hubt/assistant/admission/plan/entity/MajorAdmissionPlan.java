package com.hubt.assistant.admission.plan.entity;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.program.entity.Program;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(
        name = "major_admission_plans",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class MajorAdmissionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
@OneToMany(
        mappedBy = "majorAdmissionPlan",
        fetch = FetchType.LAZY
)
private List<MajorAdmissionMethod> majorAdmissionMethods =
        new ArrayList<>();

    // =========================================================
    // ADMISSION YEAR
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "admission_year_id",
            nullable = false
    )
    private AdmissionYear admissionYear;


    // =========================================================
    // MAJOR
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "major_id",
            nullable = false
    )
    private Major major;


    // =========================================================
    // PROGRAM
    // nullable according to DB
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;


    // =========================================================
    // TOTAL QUOTA
    // =========================================================

    @Column(
            name = "total_quota",
            nullable = false
    )
    private Integer totalQuota;


    // =========================================================
    // TUITION FEE
    // =========================================================

    @Column(
            name = "tuition_fee"
    )
    private BigDecimal tuitionFee;


    // =========================================================
    // EXPECTED CUTOFF
    // =========================================================

    @Column(
            name = "expected_cutoff"
    )
    private BigDecimal expectedCutoff;


    // =========================================================
    // APPLICATION OPEN
    // =========================================================

    @Column(
            name = "application_open",
            nullable = false
    )
    private Boolean applicationOpen = true;


    // =========================================================
    // NOTES
    // =========================================================

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;


    // =========================================================
    // CREATED AT
    // =========================================================

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;


    // =========================================================
    // UPDATED AT
    // =========================================================

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;
}