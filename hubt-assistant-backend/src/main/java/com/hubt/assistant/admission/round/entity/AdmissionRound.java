package com.hubt.assistant.admission.round.entity;

import com.hubt.assistant.admission.year.entity.AdmissionYear;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(
        name = "admission_rounds",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class AdmissionRound {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "admission_year_id",
            nullable = false
    )
    private AdmissionYear admissionYear;


    @Column(
            name = "round_number",
            nullable = false
    )
    private Integer roundNumber;


    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;


    @Column(name = "application_start_at")
    private Instant applicationStartAt;


    @Column(name = "application_end_at")
    private Instant applicationEndAt;


    @Column(name = "result_date")
    private LocalDate resultDate;


    @Column(name = "confirmation_deadline")
    private Instant confirmationDeadline;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private AdmissionRoundStatus status;


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
            status = AdmissionRoundStatus.ACTIVE;
        }
    }
}