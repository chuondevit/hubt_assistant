package com.hubt.assistant.identity.profile.repository;

import com.hubt.assistant.identity.profile.entity.CandidateInterest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateInterestRepository
        extends JpaRepository<CandidateInterest, UUID> {

    List<CandidateInterest>
    findAllByCandidateIdOrderByCreatedAtDesc(
            UUID candidateId
    );

    Optional<CandidateInterest>
    findByCandidateIdAndInterestCodeIgnoreCase(
            UUID candidateId,
            String interestCode
    );

    boolean existsByCandidateIdAndInterestCodeIgnoreCase(
            UUID candidateId,
            String interestCode
    );

    long countByCandidateId(
            UUID candidateId
    );
}