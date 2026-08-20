package com.hubt.assistant.admission.round.repository;

import com.hubt.assistant.admission.round.entity.AdmissionRound;
import com.hubt.assistant.admission.round.entity.AdmissionRoundStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AdmissionRoundRepository
        extends JpaRepository<AdmissionRound, UUID>,
        JpaSpecificationExecutor<AdmissionRound> {


    Optional<AdmissionRound> findById(
            UUID id
    );


    boolean
    existsByAdmissionYear_IdAndRoundNumber(
            UUID admissionYearId,
            Integer roundNumber
    );


    boolean
    existsByAdmissionYear_IdAndRoundNumberAndIdNot(
            UUID admissionYearId,
            Integer roundNumber,
            UUID id
    );


    List<AdmissionRound>
    findAllByAdmissionYear_IdAndStatusOrderByRoundNumberAsc(
            UUID admissionYearId,
            AdmissionRoundStatus status
    );
}