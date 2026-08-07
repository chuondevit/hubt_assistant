package com.hubt.assistant.identity.profile.repository;

import com.hubt.assistant.identity.profile.entity.CandidateAcademicProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateAcademicProfileRepository
        extends JpaRepository<CandidateAcademicProfile, UUID> {

    Optional<CandidateAcademicProfile>
    findTopByCandidateIdOrderByVersionDesc(
            UUID candidateId
    );

    boolean existsByCandidateIdAndVersion(
            UUID candidateId,
            Integer version
    );
}