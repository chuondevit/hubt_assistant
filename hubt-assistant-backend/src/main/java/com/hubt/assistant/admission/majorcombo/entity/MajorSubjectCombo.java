package com.hubt.assistant.admission.majorcombo.entity;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "major_subject_combos",
        schema = "hubt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_major_subject_combo",
                        columnNames = {
                                "major_admission_method_id",
                                "subject_combo_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MajorSubjectCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "major_admission_method_id",
            nullable = false
    )
    private MajorAdmissionMethod majorAdmissionMethod;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "subject_combo_id",
            nullable = false
    )
    private SubjectCombination subjectCombination;

    @Column(
            name = "minimum_score",
            precision = 5,
            scale = 2
    )
    private BigDecimal minimumScore;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private MajorSubjectComboStatus status;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = MajorSubjectComboStatus.ACTIVE;
        }
    }
}