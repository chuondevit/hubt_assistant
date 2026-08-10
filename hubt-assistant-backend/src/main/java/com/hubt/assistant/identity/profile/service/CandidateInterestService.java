package com.hubt.assistant.identity.profile.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.common.exception.ResourceNotFoundException;

import com.hubt.assistant.identity.profile.dto.request.CreateCandidateInterestRequest;
import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateInterestRequest;

import com.hubt.assistant.identity.profile.dto.response.CandidateInterestResponse;

import com.hubt.assistant.identity.profile.entity.CandidateInterest;
import com.hubt.assistant.identity.profile.entity.InterestLevel;

import com.hubt.assistant.identity.profile.repository.CandidateInterestRepository;

import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CandidateInterestService {

    private static final int MAX_INTERESTS = 20;


    private final CandidateInterestRepository
            candidateInterestRepository;

    private final UserRepository
            userRepository;


    // =========================================================
    // GET MY INTERESTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<CandidateInterestResponse> getMyInterests(
            UUID userId
    ) {

        getUser(
                userId
        );


        return candidateInterestRepository
                .findAllByCandidateIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // CREATE INTEREST
    // =========================================================

    @Transactional
    public CandidateInterestResponse createInterest(
            UUID userId,
            CreateCandidateInterestRequest request
    ) {

        User user =
                getUser(
                        userId
                );


        String interestCode =
                normalizeCode(
                        request.interestCode()
                );


        // =====================================================
        // CHECK DUPLICATE
        // =====================================================

        if (candidateInterestRepository
                .existsByCandidateIdAndInterestCodeIgnoreCase(
                        userId,
                        interestCode
                )) {

            throw new BusinessException(
                    "INTEREST_ALREADY_EXISTS",
                    "Sở thích này đã tồn tại"
            );
        }


        // =====================================================
        // CHECK MAX INTEREST
        // =====================================================

        long currentCount =
                candidateInterestRepository
                        .countByCandidateId(
                                userId
                        );


        if (currentCount >= MAX_INTERESTS) {

            throw new BusinessException(
                    "INTEREST_LIMIT_EXCEEDED",
                    "Mỗi thí sinh chỉ được lưu tối đa "
                            + MAX_INTERESTS
                            + " sở thích"
            );
        }


        // =====================================================
        // CREATE ENTITY
        // =====================================================

        CandidateInterest interest =
                new CandidateInterest();


        interest.setCandidate(
                user
        );


        interest.setInterestCode(
                interestCode
        );


        interest.setInterestName(
                normalizeRequired(
                        request.interestName()
                )
        );


        interest.setLevel(
                parseLevel(
                        request.level()
                )
        );


        interest.setSource(
                normalizeNullable(
                        request.source()
                )
        );


        interest.setCreatedAt(
                Instant.now()
        );


        CandidateInterest saved =
                candidateInterestRepository
                        .save(
                                interest
                        );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // UPDATE INTEREST
    // =========================================================

    @Transactional
    public CandidateInterestResponse updateInterest(
            UUID userId,
            String interestCode,
            UpdateCandidateInterestRequest request
    ) {

        getUser(
                userId
        );


        CandidateInterest interest =
                getInterest(
                        userId,
                        interestCode
                );


        // =====================================================
        // UPDATE NAME
        // =====================================================

        if (request.interestName() != null) {

            interest.setInterestName(
                    normalizeRequired(
                            request.interestName()
                    )
            );
        }


        // =====================================================
        // UPDATE LEVEL
        // =====================================================

        if (request.level() != null) {

            interest.setLevel(
                    parseLevel(
                            request.level()
                    )
            );
        }


        // =====================================================
        // UPDATE SOURCE
        // =====================================================

        if (request.source() != null) {

            interest.setSource(
                    normalizeNullable(
                            request.source()
                    )
            );
        }


        CandidateInterest saved =
                candidateInterestRepository
                        .save(
                                interest
                        );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // DELETE INTEREST
    // =========================================================

    @Transactional
    public void deleteInterest(
            UUID userId,
            String interestCode
    ) {

        getUser(
                userId
        );


        CandidateInterest interest =
                getInterest(
                        userId,
                        interestCode
                );


        candidateInterestRepository
                .delete(
                        interest
                );
    }


    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(
            UUID userId
    ) {

        return userRepository
                .findById(
                        userId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "USER_NOT_FOUND",
                                        "Không tìm thấy người dùng"
                                )
                );
    }


    // =========================================================
    // GET INTEREST
    // =========================================================

    private CandidateInterest getInterest(
            UUID userId,
            String interestCode
    ) {

        String normalizedCode =
                normalizeCode(
                        interestCode
                );


        return candidateInterestRepository
                .findByCandidateIdAndInterestCodeIgnoreCase(
                        userId,
                        normalizedCode
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "INTEREST_NOT_FOUND",
                                        "Không tìm thấy sở thích"
                                )
                );
    }


    // =========================================================
    // NORMALIZE INTEREST CODE
    // =========================================================

    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "INTEREST_CODE_REQUIRED",
                    "Mã sở thích không được để trống"
            );
        }


        String result =
                value
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        );


        if (!result.matches(
                "^[A-Z0-9_-]+$"
        )) {

            throw new BusinessException(
                    "INTEREST_CODE_INVALID",
                    "Mã sở thích không hợp lệ"
            );
        }


        if (result.length() > 100) {

            throw new BusinessException(
                    "INTEREST_CODE_TOO_LONG",
                    "Mã sở thích không được vượt quá 100 ký tự"
            );
        }


        return result;
    }


    // =========================================================
    // PARSE INTEREST LEVEL
    // =========================================================

    private InterestLevel parseLevel(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "INTEREST_LEVEL_REQUIRED",
                    "Mức độ quan tâm không được để trống"
            );
        }


        try {

            return InterestLevel.valueOf(
                    value
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            )
            );

        } catch (IllegalArgumentException ex) {

            throw new BusinessException(
                    "INTEREST_LEVEL_INVALID",
                    "Mức độ quan tâm phải là LOW, MEDIUM, HIGH hoặc VERY_HIGH"
            );
        }
    }


    // =========================================================
    // NORMALIZE REQUIRED
    // =========================================================

    private String normalizeRequired(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "INTEREST_NAME_REQUIRED",
                    "Tên sở thích không được để trống"
            );
        }


        String result =
                value.trim();


        if (result.length() > 255) {

            throw new BusinessException(
                    "INTEREST_NAME_TOO_LONG",
                    "Tên sở thích không được vượt quá 255 ký tự"
            );
        }


        return result;
    }


    // =========================================================
    // NORMALIZE NULLABLE
    // =========================================================

    private String normalizeNullable(
            String value
    ) {

        if (value == null) {

            return null;
        }


        String result =
                value.trim();


        if (result.isBlank()) {

            return null;
        }


        if (result.length() > 100) {

            throw new BusinessException(
                    "INTEREST_SOURCE_TOO_LONG",
                    "Nguồn dữ liệu không được vượt quá 100 ký tự"
            );
        }


        return result;
    }


    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private CandidateInterestResponse toResponse(
            CandidateInterest interest
    ) {

        return new CandidateInterestResponse(

                interest.getId(),

                interest.getCandidate()
                        .getId(),

                interest.getInterestCode(),

                interest.getInterestName(),

                interest.getLevel() == null
                        ? null
                        : interest.getLevel()
                        .name(),

                interest.getSource(),

                interest.getCreatedAt()
        );
    }
}