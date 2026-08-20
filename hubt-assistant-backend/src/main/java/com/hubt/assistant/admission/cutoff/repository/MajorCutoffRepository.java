package com.hubt.assistant.admission.cutoff.repository;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoff;
import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MajorCutoffRepository
        extends JpaRepository<MajorCutoff, UUID>,
        JpaSpecificationExecutor<MajorCutoff> {

    Optional<MajorCutoff>
    findTopByAdmissionYear_IdAndMajor_IdAndAdmissionMethod_IdAndSubjectCombination_IdAndStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(

            UUID admissionYearId,

            UUID majorId,

            UUID admissionMethodId,

            UUID subjectComboId,

            MajorCutoffStatus status
    );
}