package com.hubt.assistant.admission.year.repository;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AdmissionYearRepository
        extends JpaRepository<AdmissionYear, UUID>,
        JpaSpecificationExecutor<AdmissionYear> {


    Optional<AdmissionYear>
    findById(UUID id);


    boolean
    existsByUniversity_IdAndYear(
            UUID universityId,
            Integer year
    );


    boolean
    existsByUniversity_IdAndYearAndIdNot(
            UUID universityId,
            Integer year,
            UUID id
    );


    List<AdmissionYear>
    findAllByUniversity_IdAndStatusOrderByYearDesc(
            UUID universityId,
            AdmissionYearStatus status
    );


    List<AdmissionYear>
    findAllByStatusOrderByYearDesc(
            AdmissionYearStatus status
    );
}