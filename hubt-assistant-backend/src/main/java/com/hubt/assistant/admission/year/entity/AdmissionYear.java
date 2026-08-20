package com.hubt.assistant.admission.year.entity;

import com.hubt.assistant.organization.university.entity.University;

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
        name = "admission_years",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class AdmissionYear {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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


    @Column(
            name = "year",
            nullable = false
    )
    private Integer year;


    @Column(
            name = "name",
            length = 255
    )
    private String name;


    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "end_date")
    private LocalDate endDate;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.admission_year_status"
    )
    private AdmissionYearStatus status;


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
            status = AdmissionYearStatus.DRAFT;
        }
    }
}