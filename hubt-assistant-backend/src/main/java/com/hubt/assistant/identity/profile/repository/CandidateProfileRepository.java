package com.hubt.assistant.identity.profile.repository;

import com.hubt.assistant.identity.profile.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository
        extends JpaRepository<CandidateProfile, UUID> {

    Optional<CandidateProfile> findByUserId(
            UUID userId
    );

    boolean existsByIdentityNumber(
            String identityNumber
    );

    boolean existsByIdentityNumberAndUserIdNot(
            String identityNumber,
            UUID userId
    );
}