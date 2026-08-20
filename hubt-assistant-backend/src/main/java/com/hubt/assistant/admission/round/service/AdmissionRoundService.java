package com.hubt.assistant.admission.round.service;

import com.hubt.assistant.admission.round.dto.request.CreateAdmissionRoundRequest;
import com.hubt.assistant.admission.round.dto.request.UpdateAdmissionRoundRequest;

import com.hubt.assistant.admission.round.dto.response.AdmissionRoundResponse;

import com.hubt.assistant.admission.round.entity.AdmissionRound;
import com.hubt.assistant.admission.round.entity.AdmissionRoundStatus;

import com.hubt.assistant.admission.round.repository.AdmissionRoundRepository;
import com.hubt.assistant.admission.round.specification.AdmissionRoundSpecification;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;

import com.hubt.assistant.admission.year.repository.AdmissionYearRepository;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.university.entity.UniversityStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdmissionRoundService {

    private static final int MAX_PAGE_SIZE = 100;


    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "roundNumber",
                    "name",
                    "applicationStartAt",
                    "applicationEndAt",
                    "resultDate",
                    "confirmationDeadline",
                    "status",
                    "createdAt",
                    "updatedAt"
            );


    private final AdmissionRoundRepository
            admissionRoundRepository;

    private final AdmissionYearRepository
            admissionYearRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public AdmissionRoundResponse create(
            CreateAdmissionRoundRequest request
    ) {

        AdmissionYear admissionYear =
                getAdmissionYear(
                        request.admissionYearId()
                );


        validateUniversityActive(
                admissionYear
        );


        validateApplicationDates(
                request.applicationStartAt(),
                request.applicationEndAt()
        );


        if (admissionRoundRepository
                .existsByAdmissionYear_IdAndRoundNumber(
                        admissionYear.getId(),
                        request.roundNumber()
                )) {

            throw new BusinessException(
                    "ADMISSION_ROUND_EXISTS",
                    "Số đợt tuyển sinh đã tồn tại trong năm tuyển sinh này"
            );
        }


        AdmissionRound round =
                new AdmissionRound();


        round.setAdmissionYear(
                admissionYear
        );


        round.setRoundNumber(
                request.roundNumber()
        );


        round.setName(
                normalizeRequired(
                        request.name()
                )
        );


        round.setApplicationStartAt(
                request.applicationStartAt()
        );


        round.setApplicationEndAt(
                request.applicationEndAt()
        );


        round.setResultDate(
                request.resultDate()
        );


        round.setConfirmationDeadline(
                request.confirmationDeadline()
        );


        round.setStatus(
                AdmissionRoundStatus.ACTIVE
        );


        return toResponse(
                admissionRoundRepository.save(
                        round
                )
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<AdmissionRoundResponse>
    getAdminRounds(

            String keyword,

            UUID universityId,

            UUID admissionYearId,

            AdmissionRoundStatus status,

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


        Specification<AdmissionRound> specification =
                AdmissionRoundSpecification
                        .hasKeyword(
                                keyword
                        )
                        .and(
                                AdmissionRoundSpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                AdmissionRoundSpecification
                                        .hasAdmissionYearId(
                                                admissionYearId
                                        )
                        )
                        .and(
                                AdmissionRoundSpecification
                                        .hasStatus(
                                                status
                                        )
                        );


        Page<AdmissionRoundResponse> result =
                admissionRoundRepository
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
    public AdmissionRoundResponse getById(
            UUID id
    ) {

        return toResponse(
                getRound(
                        id
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public AdmissionRoundResponse update(

            UUID id,

            UpdateAdmissionRoundRequest request

    ) {

        AdmissionRound round =
                getRound(
                        id
                );


        Integer targetRoundNumber =
                request.roundNumber() == null
                        ? round.getRoundNumber()
                        : request.roundNumber();


        if (admissionRoundRepository
                .existsByAdmissionYear_IdAndRoundNumberAndIdNot(
                        round.getAdmissionYear()
                                .getId(),
                        targetRoundNumber,
                        id
                )) {

            throw new BusinessException(
                    "ADMISSION_ROUND_EXISTS",
                    "Số đợt tuyển sinh đã tồn tại trong năm tuyển sinh này"
            );
        }


        Instant startAt =
                request.applicationStartAt() == null
                        ? round.getApplicationStartAt()
                        : request.applicationStartAt();


        Instant endAt =
                request.applicationEndAt() == null
                        ? round.getApplicationEndAt()
                        : request.applicationEndAt();


        validateApplicationDates(
                startAt,
                endAt
        );


        if (request.roundNumber() != null) {

            round.setRoundNumber(
                    request.roundNumber()
            );
        }


        if (request.name() != null) {

            round.setName(
                    normalizeRequired(
                            request.name()
                    )
            );
        }


        if (request.applicationStartAt() != null) {

            round.setApplicationStartAt(
                    request.applicationStartAt()
            );
        }


        if (request.applicationEndAt() != null) {

            round.setApplicationEndAt(
                    request.applicationEndAt()
            );
        }


        if (request.resultDate() != null) {

            round.setResultDate(
                    request.resultDate()
            );
        }


        if (request.confirmationDeadline() != null) {

            round.setConfirmationDeadline(
                    request.confirmationDeadline()
            );
        }


        return toResponse(
                admissionRoundRepository.save(
                        round
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public AdmissionRoundResponse updateStatus(

            UUID id,

            AdmissionRoundStatus status

    ) {

        if (status == null) {

            throw new BusinessException(
                    "ADMISSION_ROUND_STATUS_REQUIRED",
                    "Trạng thái đợt tuyển sinh không được để trống"
            );
        }


        AdmissionRound round =
                getRound(
                        id
                );


        round.setStatus(
                status
        );


        return toResponse(
                admissionRoundRepository.save(
                        round
                )
        );
    }


    // =========================================================
    // DELETE -> INACTIVE
    // admission_rounds không có deleted_at
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        AdmissionRound round =
                getRound(
                        id
                );


        round.setStatus(
                AdmissionRoundStatus.INACTIVE
        );


        admissionRoundRepository.save(
                round
        );
    }


    // =========================================================
    // PUBLIC
    // =========================================================

    @Transactional(readOnly = true)
    public List<AdmissionRoundResponse>
    getPublicRounds(
            UUID admissionYearId
    ) {

        if (admissionYearId == null) {

            throw new BusinessException(
                    "ADMISSION_YEAR_ID_REQUIRED",
                    "Admission Year ID không được để trống"
            );
        }


        AdmissionYear admissionYear =
                getAdmissionYear(
                        admissionYearId
                );


        /*
         * Public chỉ công khai round của năm
         * tuyển sinh đang OPEN.
         */
        if (admissionYear.getStatus()
                != AdmissionYearStatus.OPEN
                ||
                admissionYear.getUniversity()
                        .getStatus()
                        != UniversityStatus.ACTIVE) {

            return List.of();
        }


        return admissionRoundRepository
                .findAllByAdmissionYear_IdAndStatusOrderByRoundNumberAsc(
                        admissionYearId,
                        AdmissionRoundStatus.ACTIVE
                )
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // INTERNAL
    // =========================================================

    private AdmissionRound getRound(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "ADMISSION_ROUND_ID_REQUIRED",
                    "ID đợt tuyển sinh không được để trống"
            );
        }


        return admissionRoundRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "ADMISSION_ROUND_NOT_FOUND",
                                        "Không tìm thấy đợt tuyển sinh"
                                )
                );
    }


    private AdmissionYear getAdmissionYear(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "ADMISSION_YEAR_ID_REQUIRED",
                    "Admission Year ID không được để trống"
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


    private void validateUniversityActive(
            AdmissionYear admissionYear
    ) {

        if (admissionYear
                .getUniversity()
                .getStatus()
                != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "UNIVERSITY_NOT_ACTIVE",
                    "Trường đại học hiện không hoạt động"
            );
        }
    }


    private void validateApplicationDates(

            Instant start,

            Instant end

    ) {

        if (start != null
                && end != null
                && end.isBefore(start)) {

            throw new BusinessException(
                    "ADMISSION_ROUND_DATE_INVALID",
                    "Thời gian kết thúc nhận hồ sơ không được trước thời gian bắt đầu"
            );
        }
    }


    private AdmissionRoundResponse toResponse(
            AdmissionRound round
    ) {

        AdmissionYear year =
                round.getAdmissionYear();


        return new AdmissionRoundResponse(

                round.getId(),

                year.getId(),

                year.getYear(),

                year.getName(),

                year.getUniversity().getId(),

                year.getUniversity().getCode(),

                round.getRoundNumber(),

                round.getName(),

                round.getApplicationStartAt(),

                round.getApplicationEndAt(),

                round.getResultDate(),

                round.getConfirmationDeadline(),

                round.getStatus() == null
                        ? null
                        : round.getStatus().name(),

                round.getCreatedAt(),

                round.getUpdatedAt()
        );
    }


    private String normalizeRequired(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "ADMISSION_ROUND_NAME_REQUIRED",
                    "Tên đợt tuyển sinh không được để trống"
            );
        }


        return value.trim();
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
                        ? "roundNumber"
                        : sortBy.trim();


        if (!ALLOWED_SORT_FIELDS
                .contains(field)) {

            throw new BusinessException(
                    "INVALID_SORT_FIELD",
                    "Trường sắp xếp không hợp lệ"
            );
        }


        Sort.Direction direction;


        if (sortDirection == null
                || sortDirection.isBlank()
                || "asc".equalsIgnoreCase(
                        sortDirection
                )) {

            direction =
                    Sort.Direction.ASC;

        } else if ("desc".equalsIgnoreCase(
                sortDirection
        )) {

            direction =
                    Sort.Direction.DESC;

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