package com.hubt.assistant.admission.subject.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(
        name = "subjects",
        schema = "hubt"
)
@Getter
@Setter
@NoArgsConstructor
public class Subject {

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
}