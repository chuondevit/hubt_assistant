package com.hubt.assistant.admission.planmethod.repository;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface MajorAdmissionMethodRepository
        extends JpaRepository<MajorAdmissionMethod, UUID>,
        JpaSpecificationExecutor<MajorAdmissionMethod> {

    boolean
    existsByMajorAdmissionPlan_IdAndAdmissionMethod_Id(
            UUID majorAdmissionPlanId,
            UUID admissionMethodId
    );

    boolean
    existsByMajorAdmissionPlan_IdAndAdmissionMethod_IdAndIdNot(
            UUID majorAdmissionPlanId,
            UUID admissionMethodId,
            UUID id
    );

    List<MajorAdmissionMethod>
    findAllByMajorAdmissionPlan_IdAndStatusOrderByAdmissionMethod_NameAsc(
            UUID planId,
            MajorAdmissionMethodStatus status
    );
}