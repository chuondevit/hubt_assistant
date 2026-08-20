package com.hubt.assistant.admission.plan.repository;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.UUID;

public interface MajorAdmissionPlanRepository
        extends JpaRepository<MajorAdmissionPlan, UUID>,
        JpaSpecificationExecutor<MajorAdmissionPlan> {
List<MajorAdmissionPlan>
findAllByMajor_IdAndAdmissionYear_IdAndApplicationOpenTrueOrderByCreatedAtDesc(
        UUID majorId,
        UUID admissionYearId
);
    boolean existsByAdmissionYearIdAndMajorIdAndProgramId(
            UUID admissionYearId,
            UUID majorId,
            UUID programId
    );

    boolean existsByAdmissionYearIdAndMajorIdAndProgramIsNull(
            UUID admissionYearId,
            UUID majorId
    );

    boolean existsByAdmissionYearIdAndMajorIdAndProgramIdAndIdNot(
            UUID admissionYearId,
            UUID majorId,
            UUID programId,
            UUID id
    );

    boolean existsByAdmissionYearIdAndMajorIdAndProgramIsNullAndIdNot(
            UUID admissionYearId,
            UUID majorId,
            UUID id
    );
}