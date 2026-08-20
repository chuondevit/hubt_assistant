package com.hubt.assistant.admission.combination.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubjectCombinationItemId
        implements Serializable {

    private UUID subjectCombination;

    private UUID subject;
}