package com.hubt.assistant.identity.profile.service;

import com.hubt.assistant.common.exception.ResourceNotFoundException;

import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateAcademicProfileRequest;
import com.hubt.assistant.identity.profile.dto.response.CandidateAcademicProfileResponse;

import com.hubt.assistant.identity.profile.entity.CandidateAcademicProfile;
import com.hubt.assistant.identity.profile.repository.CandidateAcademicProfileRepository;

import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CandidateAcademicProfileService {

    private final UserRepository userRepository;

    private final CandidateAcademicProfileRepository
            academicProfileRepository;


    // =========================================================
    // GET CURRENT ACADEMIC PROFILE
    // =========================================================

    @Transactional(readOnly = true)
    public CandidateAcademicProfileResponse
    getMyAcademicProfile(
            UUID userId
    ) {

        getUser(userId);

        CandidateAcademicProfile profile =
                academicProfileRepository
                        .findTopByCandidateIdOrderByVersionDesc(
                                userId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "ACADEMIC_PROFILE_NOT_FOUND",
                                                "Chưa có hồ sơ học tập"
                                        )
                        );

        return toResponse(profile);
    }


    // =========================================================
    // UPDATE ACADEMIC PROFILE
    //
    // Không update record cũ.
    // Tạo VERSION mới để lưu lịch sử.
    // =========================================================

    @Transactional
    public CandidateAcademicProfileResponse
    updateMyAcademicProfile(
            UUID userId,
            UpdateCandidateAcademicProfileRequest request
    ) {

        User user =
                getUser(userId);


        CandidateAcademicProfile currentProfile =
                academicProfileRepository
                        .findTopByCandidateIdOrderByVersionDesc(
                                userId
                        )
                        .orElse(null);


        int nextVersion =
                currentProfile == null
                        ? 1
                        : currentProfile.getVersion() + 1;


        CandidateAcademicProfile profile =
                new CandidateAcademicProfile();

        profile.setCandidate(
                user
        );

        profile.setVersion(
                nextVersion
        );

        profile.setMathScore(
                request.mathScore()
        );

        profile.setLiteratureScore(
                request.literatureScore()
        );

        profile.setForeignLanguageScore(
                request.foreignLanguageScore()
        );

        profile.setNaturalScienceScore(
                request.naturalScienceScore()
        );

        profile.setSocialScienceScore(
                request.socialScienceScore()
        );

        profile.setTechnologyScore(
                request.technologyScore()
        );

        profile.setAverageScore(
                request.averageScore()
        );

        profile.setCreatedAt(
                Instant.now()
        );


        CandidateAcademicProfile saved =
                academicProfileRepository.save(
                        profile
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(
            UUID userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "USER_NOT_FOUND",
                                        "Không tìm thấy người dùng"
                                )
                );
    }


    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private CandidateAcademicProfileResponse
    toResponse(
            CandidateAcademicProfile profile
    ) {

        return new CandidateAcademicProfileResponse(

                profile.getId(),

                profile.getCandidate()
                        .getId(),

                profile.getVersion(),

                profile.getMathScore(),

                profile.getLiteratureScore(),

                profile.getForeignLanguageScore(),

                profile.getNaturalScienceScore(),

                profile.getSocialScienceScore(),

                profile.getTechnologyScore(),

                profile.getAverageScore(),

                profile.getCreatedAt()
        );
    }
}