package com.hubt.assistant.identity.profile.service;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.common.exception.ResourceNotFoundException;

import com.hubt.assistant.identity.profile.dto.request.AdminCandidateFilter;
import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateStatusRequest;

import com.hubt.assistant.identity.profile.dto.response.AdminCandidateDetailResponse;
import com.hubt.assistant.identity.profile.dto.response.AdminCandidateSummaryResponse;

import com.hubt.assistant.identity.profile.entity.CandidateProfile;

import com.hubt.assistant.identity.profile.repository.CandidateProfileRepository;

import com.hubt.assistant.identity.profile.specification.CandidateProfileSpecification;

import com.hubt.assistant.identity.user.entity.AccountStatus;
import com.hubt.assistant.identity.user.entity.User;

import com.hubt.assistant.identity.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdminCandidateService {

    private final CandidateProfileRepository
            candidateProfileRepository;

    private final UserRepository
            userRepository;


    // =========================================================
    // GET CANDIDATES
    // SEARCH + FILTER + PAGINATION + SORTING
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<AdminCandidateSummaryResponse>
    getCandidates(

            AdminCandidateFilter filter,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        // =====================================================
        // VALIDATE PAGE
        // =====================================================

        if (page < 0) {

            throw new BusinessException(
                    "PAGE_INVALID",
                    "Số trang không được nhỏ hơn 0"
            );
        }


        // =====================================================
        // VALIDATE SIZE
        // =====================================================

        if (size < 1
                || size > 100) {

            throw new BusinessException(
                    "PAGE_SIZE_INVALID",
                    "Kích thước trang phải từ 1 đến 100"
            );
        }


        // =====================================================
        // VALIDATE STATUS
        // =====================================================

        if (filter != null
                && filter.status() != null
                && !filter.status().isBlank()) {

            try {

                AccountStatus.valueOf(
                        filter.status()
                                .trim()
                                .toUpperCase(
                                        Locale.ROOT
                                )
                );

            } catch (IllegalArgumentException ex) {

                throw new BusinessException(
                        "ACCOUNT_STATUS_INVALID",
                        "Trạng thái tài khoản không hợp lệ"
                );
            }
        }


        // =====================================================
        // SORT FIELD
        // =====================================================

        String safeSortBy =
                resolveSortField(
                        sortBy
                );


        // =====================================================
        // SORT DIRECTION
        // =====================================================

        Sort.Direction direction =
                resolveSortDirection(
                        sortDirection
                );


        // =====================================================
        // PAGEABLE
        // =====================================================

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                direction,
                                safeSortBy
                        )
                );


        // =====================================================
        // QUERY
        // =====================================================

        AdminCandidateFilter safeFilter =
                filter == null
                        ? new AdminCandidateFilter(
                                null,
                                null,
                                null
                        )
                        : filter;


        Page<CandidateProfile> profilePage =
                candidateProfileRepository
                        .findAll(
                                CandidateProfileSpecification
                                        .filter(
                                                safeFilter
                                        ),
                                pageable
                        );


        // =====================================================
        // MAP RESPONSE
        // =====================================================

        Page<AdminCandidateSummaryResponse>
                responsePage =
                profilePage.map(
                        this::toSummaryResponse
                );


        return PageResponse.from(
                responsePage
        );
    }


    // =========================================================
    // GET CANDIDATE DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public AdminCandidateDetailResponse
    getCandidateDetail(
            UUID candidateId
    ) {

        CandidateProfile profile =
                getProfileByUserId(
                        candidateId
                );


        return toDetailResponse(
                profile
        );
    }


    // =========================================================
    // UPDATE CANDIDATE STATUS
    // =========================================================

    @Transactional
    public AdminCandidateDetailResponse
    updateStatus(

            UUID candidateId,

            UpdateCandidateStatusRequest request

    ) {

        CandidateProfile profile =
                getProfileByUserId(
                        candidateId
                );


        User user =
                profile.getUser();


        AccountStatus newStatus =
                parseStatus(
                        request.status()
                );


        user.setAccountStatus(
                newStatus
        );


        userRepository.save(
                user
        );


        return toDetailResponse(
                profile
        );
    }


    // =========================================================
    // GET PROFILE BY USER ID
    // =========================================================

    private CandidateProfile
    getProfileByUserId(
            UUID candidateId
    ) {

        return candidateProfileRepository
                .findByUserId(
                        candidateId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "CANDIDATE_NOT_FOUND",
                                        "Không tìm thấy thí sinh"
                                )
                );
    }


    // =========================================================
    // PARSE ACCOUNT STATUS
    // =========================================================

    private AccountStatus
    parseStatus(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "ACCOUNT_STATUS_REQUIRED",
                    "Trạng thái tài khoản không được để trống"
            );
        }


        try {

            return AccountStatus.valueOf(
                    value
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            )
            );

        } catch (IllegalArgumentException ex) {

            throw new BusinessException(
                    "ACCOUNT_STATUS_INVALID",
                    "Trạng thái tài khoản không hợp lệ"
            );
        }
    }


    // =========================================================
    // RESOLVE SORT FIELD
    // =========================================================

    private String
    resolveSortField(
            String sortBy
    ) {

        if (sortBy == null
                || sortBy.isBlank()) {

            return "createdAt";
        }


        return switch (
                sortBy
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        )
        ) {

            case "createdat" ->
                    "createdAt";

            case "candidatecode" ->
                    "candidateCode";

            case "profilecompletionpercent" ->
                    "profileCompletionPercent";

            default ->
                    throw new BusinessException(
                            "SORT_FIELD_INVALID",
                            "Trường sắp xếp không hợp lệ"
                    );
        };
    }


    // =========================================================
    // RESOLVE SORT DIRECTION
    // =========================================================

    private Sort.Direction
    resolveSortDirection(
            String sortDirection
    ) {

        if (sortDirection == null
                || sortDirection.isBlank()) {

            return Sort.Direction.DESC;
        }


        if ("asc".equalsIgnoreCase(
                sortDirection
        )) {

            return Sort.Direction.ASC;
        }


        if ("desc".equalsIgnoreCase(
                sortDirection
        )) {

            return Sort.Direction.DESC;
        }


        throw new BusinessException(
                "SORT_DIRECTION_INVALID",
                "Hướng sắp xếp chỉ được là asc hoặc desc"
        );
    }


    // =========================================================
    // SUMMARY RESPONSE
    // =========================================================

    private AdminCandidateSummaryResponse
    toSummaryResponse(
            CandidateProfile profile
    ) {

        User user =
                profile.getUser();


        return new AdminCandidateSummaryResponse(

                user.getId(),

                profile.getCandidateCode(),

                user.getFullName(),

                user.getEmail(),

                user.getPhone(),

                user.getAccountStatus() == null
                        ? null
                        : user.getAccountStatus()
                        .name(),

                user.isEmailVerified(),

                user.isPhoneVerified(),

                profile.getProfileCompletionPercent(),

                profile.getCreatedAt()
        );
    }


    // =========================================================
    // DETAIL RESPONSE
    // =========================================================

    private AdminCandidateDetailResponse
    toDetailResponse(
            CandidateProfile profile
    ) {

        User user =
                profile.getUser();


        return new AdminCandidateDetailResponse(

                user.getId(),

                profile.getCandidateCode(),

                user.getFullName(),

                user.getEmail(),

                user.getPhone(),

                user.getDateOfBirth(),

                user.getGender() == null
                        ? null
                        : user.getGender()
                        .name(),

                user.getAvatarUrl(),

                user.getAccountStatus() == null
                        ? null
                        : user.getAccountStatus()
                        .name(),

                user.isEmailVerified(),

                user.isPhoneVerified(),

                profile.getIdentityNumber(),

                profile.getSchoolName(),

                profile.getProvinceCode(),

                profile.getDistrictCode(),

                profile.getGraduationYear(),

                profile.getEducationLevel(),

                profile.getCareerGoal(),

                profile.getPreferredStudyLocation(),

                profile.getProfileCompletionPercent(),

                profile.getCreatedAt(),

                profile.getUpdatedAt()
        );
    }
}