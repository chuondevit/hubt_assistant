package com.hubt.assistant.admission.method.entity;

import com.hubt.assistant.organization.university.entity.University;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(
        name = "admission_methods",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class AdmissionMethod {

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


    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "hubt.generic_status"
    )
    private AdmissionMethodStatus status;


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
            status = AdmissionMethodStatus.ACTIVE;
        }
    }
}