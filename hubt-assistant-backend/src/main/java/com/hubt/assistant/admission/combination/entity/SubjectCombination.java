package com.hubt.assistant.admission.combination.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "subject_combos",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class SubjectCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "code",
            nullable = false,
            unique = true,
            length = 30
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

    @OneToMany(
            mappedBy = "subjectCombination",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SubjectCombinationItem> items = new ArrayList<>();
}