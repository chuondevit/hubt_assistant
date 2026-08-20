package com.hubt.assistant.admission.publicapi.service;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;
import com.hubt.assistant.admission.plan.repository.MajorAdmissionPlanRepository;

import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionSearchResponse;
import com.hubt.assistant.admission.publicapi.specification.PublicAdmissionSearchSpecification;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicAdmissionSearchService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "totalQuota",
                    "tuitionFee",
                    "expectedCutoff"
            );

    private final MajorAdmissionPlanRepository
            majorAdmissionPlanRepository;


    @Transactional(readOnly = true)
    public PageResponse<PublicAdmissionSearchResponse> search(

            String keyword,

            UUID universityId,

            UUID admissionYearId,

            UUID admissionMethodId,

            UUID subjectComboId,

            BigDecimal minCutoff,

            BigDecimal maxCutoff,

            Boolean applicationOpen,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        validatePagination(
                page,
                size
        );

        if (minCutoff != null
                && maxCutoff != null
                && minCutoff.compareTo(maxCutoff) > 0) {

            throw new BusinessException(
                    "INVALID_CUTOFF_RANGE",
                    "minCutoff không được lớn hơn maxCutoff"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        createSort(
                                sortBy,
                                sortDirection
                        )
                );

        Specification<MajorAdmissionPlan> specification =
                PublicAdmissionSearchSpecification
                        .hasKeyword(
                                keyword
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .hasAdmissionYearId(
                                                admissionYearId
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .hasAdmissionMethodId(
                                                admissionMethodId
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .hasSubjectComboId(
                                                subjectComboId
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .minExpectedCutoff(
                                                minCutoff
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .maxExpectedCutoff(
                                                maxCutoff
                                        )
                        )
                        .and(
                                PublicAdmissionSearchSpecification
                                        .hasApplicationOpen(
                                                applicationOpen
                                        )
                        );

        Page<PublicAdmissionSearchResponse> result =
                majorAdmissionPlanRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(
                                this::toResponse
                        );

        return PageResponse.from(
                result
        );
    }


    private PublicAdmissionSearchResponse toResponse(
            MajorAdmissionPlan plan
    ) {

        var major =
                plan.getMajor();

        var university =
                major.getUniversity();

        var year =
                plan.getAdmissionYear();

        var program =
                plan.getProgram();

        return new PublicAdmissionSearchResponse(

                plan.getId(),

                university.getId(),
                university.getCode(),
                university.getName(),

                major.getId(),
                major.getCode(),
                major.getName(),

                program == null
                        ? null
                        : program.getId(),

                program == null
                        ? null
                        : program.getCode(),

                program == null
                        ? null
                        : program.getName(),

                year.getId(),
                year.getYear(),

                plan.getTotalQuota(),

                plan.getTuitionFee(),

                plan.getExpectedCutoff(),

                plan.getApplicationOpen()
        );
    }


    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new BusinessException(
                    "INVALID_PAGE",
                    "Page phải lớn hơn hoặc bằng 0"
            );
        }

        if (size < 1
                || size > MAX_PAGE_SIZE) {

            throw new BusinessException(
                    "INVALID_PAGE_SIZE",
                    "Size phải từ 1 đến 100"
            );
        }
    }


    private Sort createSort(

            String sortBy,

            String sortDirection

    ) {

        String field =
                sortBy == null
                        || sortBy.isBlank()
                        ? "createdAt"
                        : sortBy.trim();

        if (!ALLOWED_SORT_FIELDS.contains(
                field
        )) {

            throw new BusinessException(
                    "INVALID_SORT_FIELD",
                    "Trường sắp xếp không hợp lệ"
            );
        }

        Sort.Direction direction;

        if (sortDirection == null
                || sortDirection.isBlank()
                || "desc".equalsIgnoreCase(
                        sortDirection
                )) {

            direction =
                    Sort.Direction.DESC;

        } else if ("asc".equalsIgnoreCase(
                sortDirection
        )) {

            direction =
                    Sort.Direction.ASC;

        } else {

            throw new BusinessException(
                    "INVALID_SORT_DIRECTION",
                    "sortDirection chỉ nhận asc hoặc desc"
            );
        }

        return Sort.by(
                direction,
                field
        );
    }
}