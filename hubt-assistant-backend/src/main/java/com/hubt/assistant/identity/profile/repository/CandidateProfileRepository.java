package com.hubt.assistant.identity.profile.repository;

import com.hubt.assistant.identity.profile.entity.CandidateProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository
        extends JpaRepository<CandidateProfile, UUID>,
        JpaSpecificationExecutor<CandidateProfile> {

    Optional<CandidateProfile>
    findByUserId(
            UUID userId
    );


    boolean existsByIdentityNumberAndUserIdNot(
            String identityNumber,
            UUID userId
    );
}