package com.hubt.assistant.admission.planmethod.entity;

import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectCombo;
import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Entity
@Table(
        name = "major_admission_methods",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class MajorAdmissionMethod {

    // =========================================================
    // ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    // =========================================================
    // MAJOR ADMISSION PLAN
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "major_admission_plan_id",
            nullable = false
    )
    private MajorAdmissionPlan majorAdmissionPlan;


    // =========================================================
    // ADMISSION METHOD
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "admission_method_id",
            nullable = false
    )
    private AdmissionMethod admissionMethod;


    // =========================================================
    // QUOTA
    // =========================================================

    @Column(name = "quota")
    private Integer quota;


    // =========================================================
    // MINIMUM SCORE
    // =========================================================

    @Column(
            name = "minimum_score",
            precision = 5,
            scale = 2
    )
    private BigDecimal minimumScore;


    // =========================================================
    // CONDITIONS JSON
    // PostgreSQL JSONB
    // =========================================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "conditions_json",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> conditionsJson =
            new HashMap<>();


    // =========================================================
    // STATUS
    // =========================================================

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private MajorAdmissionMethodStatus status;


    // =========================================================
    // CREATED AT
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;


    // =========================================================
    // UPDATED AT
    // =========================================================

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;


    // =========================================================
    // SUBJECT COMBINATIONS
    //
    // MajorAdmissionMethod 1 --- N MajorSubjectCombo
    //
    // mappedBy phải trùng CHÍNH XÁC tên field:
    //
    // private MajorAdmissionMethod majorAdmissionMethod;
    //
    // bên MajorSubjectCombo.java
    // =========================================================

    @OneToMany(
            mappedBy = "majorAdmissionMethod",
            fetch = FetchType.LAZY
    )
    private List<MajorSubjectCombo> subjectCombinations =
            new ArrayList<>();


    // =========================================================
    // PRE PERSIST
    // =========================================================

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
            status = MajorAdmissionMethodStatus.ACTIVE;
        }

        if (conditionsJson == null) {
            conditionsJson = new HashMap<>();
        }
    }


    // =========================================================
    // PRE UPDATE
    // =========================================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt = Instant.now();
    }
}