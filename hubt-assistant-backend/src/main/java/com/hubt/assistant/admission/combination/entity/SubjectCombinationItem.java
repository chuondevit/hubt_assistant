package com.hubt.assistant.admission.combination.entity;

import com.hubt.assistant.admission.subject.entity.Subject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "subject_combo_items",
        schema = "hubt"
)
@IdClass(SubjectCombinationItemId.class)
@Getter
@Setter
@NoArgsConstructor
public class SubjectCombinationItem {

    // =========================================================
    // SUBJECT COMBINATION
    // PK PART 1
    // =========================================================

    @Id
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "subject_combo_id",
            nullable = false
    )
    private SubjectCombination subjectCombination;


    // =========================================================
    // SUBJECT
    // PK PART 2
    // =========================================================

    @Id
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "subject_id",
            nullable = false
    )
    private Subject subject;


    // =========================================================
    // COEFFICIENT
    // =========================================================

    @Column(
            name = "coefficient",
            nullable = false,
            precision = 4,
            scale = 2
    )
    private BigDecimal coefficient;
}