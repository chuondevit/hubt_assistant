package com.hubt.assistant.admission.year.service;

import com.hubt.assistant.admission.year.dto.request.CreateAdmissionYearRequest;
import com.hubt.assistant.admission.year.dto.request.UpdateAdmissionYearRequest;

import com.hubt.assistant.admission.year.dto.response.AdmissionYearResponse;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;

import com.hubt.assistant.admission.year.repository.AdmissionYearRepository;

import com.hubt.assistant.admission.year.specification.AdmissionYearSpecification;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.university.entity.University;
import com.hubt.assistant.organization.university.entity.UniversityStatus;

import com.hubt.assistant.organization.university.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdmissionYearService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "year",
                    "name",
                    "startDate",
                    "endDate",
                    "status",
                    "createdAt",
                    "updatedAt"
            );


    private final AdmissionYearRepository
            admissionYearRepository;

    private final UniversityRepository
            universityRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public AdmissionYearResponse create(
            CreateAdmissionYearRequest request
    ) {

        University university =
                getActiveUniversity(
                        request.universityId()
                );


        validateDates(
                request.startDate(),
                request.endDate()
        );


        if (admissionYearRepository
                .existsByUniversity_IdAndYear(
                        university.getId(),
                        request.year()
                )) {

            throw new BusinessException(
                    "ADMISSION_YEAR_EXISTS",
                    "Năm tuyển sinh đã tồn tại trong trường này"
            );
        }


        AdmissionYear admissionYear =
                new AdmissionYear();


        admissionYear.setUniversity(
                university
        );

        admissionYear.setYear(
                request.year()
        );

        admissionYear.setName(
                normalizeNullable(
                        request.name()
                )
        );

        admissionYear.setStartDate(
                request.startDate()
        );

        admissionYear.setEndDate(
                request.endDate()
        );

        admissionYear.setStatus(
                AdmissionYearStatus.DRAFT
        );


        return toResponse(
                admissionYearRepository.save(
                        admissionYear
                )
        );
    }


    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<AdmissionYearResponse>
    getAdminAdmissionYears(

            UUID universityId,

            Integer year,

            AdmissionYearStatus status,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        validatePagination(
                page,
                size
        );


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        createSort(
                                sortBy,
                                sortDirection
                        )
                );


        Specification<AdmissionYear> specification =
                AdmissionYearSpecification
                        .hasUniversityId(
                                universityId
                        )
                        .and(
                                AdmissionYearSpecification
                                        .hasYear(
                                                year
                                        )
                        )
                        .and(
                                AdmissionYearSpecification
                                        .hasStatus(
                                                status
                                        )
                        );


        Page<AdmissionYearResponse> result =
                admissionYearRepository
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


    // =========================================================
    // DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public AdmissionYearResponse getById(
            UUID id
    ) {

        return toResponse(
                getAdmissionYear(
                        id
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public AdmissionYearResponse update(

            UUID id,

            UpdateAdmissionYearRequest request

    ) {

        AdmissionYear admissionYear =
                getAdmissionYear(
                        id
                );


        Integer targetYear =
                request.year() == null
                        ? admissionYear.getYear()
                        : request.year();


        if (admissionYearRepository
                .existsByUniversity_IdAndYearAndIdNot(
                        admissionYear
                                .getUniversity()
                                .getId(),
                        targetYear,
                        id
                )) {

            throw new BusinessException(
                    "ADMISSION_YEAR_EXISTS",
                    "Năm tuyển sinh đã tồn tại trong trường này"
            );
        }


        LocalDate startDate =
                request.startDate() == null
                        ? admissionYear.getStartDate()
                        : request.startDate();


        LocalDate endDate =
                request.endDate() == null
                        ? admissionYear.getEndDate()
                        : request.endDate();


        validateDates(
                startDate,
                endDate
        );


        if (request.year() != null) {
            admissionYear.setYear(
                    request.year()
            );
        }


        if (request.name() != null) {
            admissionYear.setName(
                    normalizeNullable(
                            request.name()
                    )
            );
        }


        if (request.startDate() != null) {
            admissionYear.setStartDate(
                    request.startDate()
            );
        }


        if (request.endDate() != null) {
            admissionYear.setEndDate(
                    request.endDate()
            );
        }


        return toResponse(
                admissionYearRepository.save(
                        admissionYear
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public AdmissionYearResponse updateStatus(

            UUID id,

            AdmissionYearStatus status

    ) {

        if (status == null) {

            throw new BusinessException(
                    "ADMISSION_YEAR_STATUS_REQUIRED",
                    "Trạng thái năm tuyển sinh không được để trống"
            );
        }


        AdmissionYear admissionYear =
                getAdmissionYear(
                        id
                );


        admissionYear.setStatus(
                status
        );


        return toResponse(
                admissionYearRepository.save(
                        admissionYear
                )
        );
    }


    // =========================================================
    // PUBLIC
    // =========================================================

    @Transactional(readOnly = true)
    public List<AdmissionYearResponse>
    getPublicAdmissionYears(
            UUID universityId
    ) {

        if (universityId != null) {

            return admissionYearRepository
                    .findAllByUniversity_IdAndStatusOrderByYearDesc(
                            universityId,
                            AdmissionYearStatus.OPEN
                    )
                    .stream()
                    .filter(
                            item ->
                                    item.getUniversity()
                                            .getStatus()
                                            == UniversityStatus.ACTIVE
                    )
                    .map(
                            this::toResponse
                    )
                    .toList();
        }


        return admissionYearRepository
                .findAllByStatusOrderByYearDesc(
                        AdmissionYearStatus.OPEN
                )
                .stream()
                .filter(
                        item ->
                                item.getUniversity()
                                    .getStatus()
                                    == UniversityStatus.ACTIVE
                )
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // INTERNAL
    // =========================================================

    private AdmissionYear getAdmissionYear(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "ADMISSION_YEAR_ID_REQUIRED",
                    "ID năm tuyển sinh không được để trống"
            );
        }


        return admissionYearRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "ADMISSION_YEAR_NOT_FOUND",
                                        "Không tìm thấy năm tuyển sinh"
                                )
                );
    }


    private University getActiveUniversity(
            UUID id
    ) {

        University university =
                universityRepository
                        .findByIdAndDeletedAtIsNull(
                                id
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "UNIVERSITY_NOT_FOUND",
                                                "Không tìm thấy trường đại học"
                                        )
                        );


        if (university.getStatus()
                != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "UNIVERSITY_NOT_ACTIVE",
                    "Trường đại học không hoạt động"
            );
        }


        return university;
    }


    private void validateDates(

            LocalDate startDate,

            LocalDate endDate

    ) {

        if (startDate != null
                && endDate != null
                && endDate.isBefore(
                        startDate
                )) {

            throw new BusinessException(
                    "ADMISSION_YEAR_DATE_INVALID",
                    "Ngày kết thúc không được trước ngày bắt đầu"
            );
        }
    }


    private AdmissionYearResponse toResponse(
            AdmissionYear item
    ) {

        return new AdmissionYearResponse(

                item.getId(),

                item.getUniversity().getId(),

                item.getUniversity().getCode(),

                item.getUniversity().getName(),

                item.getYear(),

                item.getName(),

                item.getStartDate(),

                item.getEndDate(),

                item.getStatus() == null
                        ? null
                        : item.getStatus().name(),

                item.getCreatedAt(),

                item.getUpdatedAt()
        );
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String result =
                value.trim();

        return result.isBlank()
                ? null
                : result;
    }


    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new BusinessException(
                    "INVALID_PAGE",
                    "Page phải >= 0"
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
                        ? "year"
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