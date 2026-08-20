package com.hubt.assistant.admission.majorcombo.repository;

import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectCombo;
import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectComboStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface MajorSubjectComboRepository
        extends JpaRepository<MajorSubjectCombo, UUID>,
        JpaSpecificationExecutor<MajorSubjectCombo> {

    boolean
    existsByMajorAdmissionMethod_IdAndSubjectCombination_Id(
            UUID majorAdmissionMethodId,
            UUID subjectCombinationId
    );

    List<MajorSubjectCombo>
    findAllByMajorAdmissionMethod_IdAndStatus(
            UUID majorAdmissionMethodId,
            MajorSubjectComboStatus status
    );
}