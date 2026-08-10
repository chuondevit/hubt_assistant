package com.hubt.assistant.identity.profile.entity;

import com.hubt.assistant.identity.user.entity.User;
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
        name = "candidate_interests",
        schema = "hubt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_candidate_interest_code",
                        columnNames = {
                                "candidate_id",
                                "interest_code"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CandidateInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "candidate_id",
            nullable = false
    )
    private User candidate;

    @Column(
            name = "interest_code",
            nullable = false,
            length = 100
    )
    private String interestCode;

    @Column(
            name = "interest_name",
            nullable = false,
            length = 255
    )
    private String interestName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "level",
            nullable = false,
            columnDefinition = "hubt.interest_level"
    )
    private InterestLevel level;

    @Column(
            name = "source",
            length = 100
    )
    private String source;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}