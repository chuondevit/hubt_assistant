package com.hubt.assistant.admission.method.repository;

import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AdmissionMethodRepository
        extends JpaRepository<AdmissionMethod, UUID>,
        JpaSpecificationExecutor<AdmissionMethod> {


    Optional<AdmissionMethod>
    findById(
            UUID id
    );


    boolean
    existsByUniversity_IdAndCodeIgnoreCase(
            UUID universityId,
            String code
    );


    boolean
    existsByUniversity_IdAndCodeIgnoreCaseAndIdNot(
            UUID universityId,
            String code,
            UUID id
    );


    List<AdmissionMethod>
    findAllByUniversity_IdAndStatusOrderByNameAsc(
            UUID universityId,
            AdmissionMethodStatus status
    );


    List<AdmissionMethod>
    findAllByStatusOrderByNameAsc(
            AdmissionMethodStatus status
    );
}