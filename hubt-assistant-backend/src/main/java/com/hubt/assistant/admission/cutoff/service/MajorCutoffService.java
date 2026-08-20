package com.hubt.assistant.admission.cutoff.service;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;
import com.hubt.assistant.admission.combination.repository.SubjectCombinationRepository;

import com.hubt.assistant.admission.cutoff.dto.request.CreateMajorCutoffRequest;
import com.hubt.assistant.admission.cutoff.dto.request.UpdateMajorCutoffRequest;

import com.hubt.assistant.admission.cutoff.dto.response.MajorCutoffResponse;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoff;
import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import com.hubt.assistant.admission.cutoff.repository.MajorCutoffRepository;
import com.hubt.assistant.admission.cutoff.specification.MajorCutoffSpecification;

import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;
import com.hubt.assistant.admission.method.repository.AdmissionMethodRepository;

import com.hubt.assistant.admission.round.entity.AdmissionRound;
import com.hubt.assistant.admission.round.repository.AdmissionRoundRepository;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.repository.AdmissionYearRepository;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.repository.MajorRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MajorCutoffService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "cutoffScore",
                    "publishedAt",
                    "createdAt",
                    "status"
            );

    private final MajorCutoffRepository
            majorCutoffRepository;

    private final AdmissionYearRepository
            admissionYearRepository;

    private final AdmissionRoundRepository
            admissionRoundRepository;

    private final MajorRepository
            majorRepository;

    private final AdmissionMethodRepository
            admissionMethodRepository;

    private final SubjectCombinationRepository
            subjectCombinationRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public MajorCutoffResponse create(
            CreateMajorCutoffRequest request
    ) {

        AdmissionYear year =
                getAdmissionYear(
                        request.admissionYearId()
                );

        Major major =
                getMajor(
                        request.majorId()
                );

        AdmissionMethod method =
                getAdmissionMethod(
                        request.admissionMethodId()
                );

        AdmissionRound round = null;

        if (request.admissionRoundId() != null) {

            round =
                    getAdmissionRound(
                            request.admissionRoundId()
                    );

            validateRoundBelongsToYear(
                    round,
                    year
            );
        }

        SubjectCombination combo = null;

        if (request.subjectComboId() != null) {

            combo =
                    getSubjectCombination(
                            request.subjectComboId()
                    );
        }

        validateMethodBelongsToMajorUniversity(
                major,
                method
        );

        MajorCutoff cutoff =
                new MajorCutoff();

        cutoff.setAdmissionYear(
                year
        );

        cutoff.setAdmissionRound(
                round
        );

        cutoff.setMajor(
                major
        );

        cutoff.setAdmissionMethod(
                method
        );

        cutoff.setSubjectCombination(
                combo
        );

        cutoff.setCutoffScore(
                request.cutoffScore()
        );

        cutoff.setPublishedAt(
                request.publishedAt()
        );

        cutoff.setStatus(
                MajorCutoffStatus.ACTIVE
        );

        return toResponse(
                majorCutoffRepository.save(
                        cutoff
                )
        );
    }


    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<MajorCutoffResponse> getAll(

            UUID admissionYearId,

            UUID admissionRoundId,

            UUID majorId,

            UUID admissionMethodId,

            UUID subjectComboId,

            MajorCutoffStatus status,

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

        Specification<MajorCutoff> specification =
                MajorCutoffSpecification
                        .hasAdmissionYearId(
                                admissionYearId
                        )
                        .and(
                                MajorCutoffSpecification
                                        .hasAdmissionRoundId(
                                                admissionRoundId
                                        )
                        )
                        .and(
                                MajorCutoffSpecification
                                        .hasMajorId(
                                                majorId
                                        )
                        )
                        .and(
                                MajorCutoffSpecification
                                        .hasAdmissionMethodId(
                                                admissionMethodId
                                        )
                        )
                        .and(
                                MajorCutoffSpecification
                                        .hasSubjectComboId(
                                                subjectComboId
                                        )
                        )
                        .and(
                                MajorCutoffSpecification
                                        .hasStatus(
                                                status
                                        )
                        );

        Page<MajorCutoffResponse> result =
                majorCutoffRepository
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
    public MajorCutoffResponse getById(
            UUID id
    ) {

        return toResponse(
                getCutoff(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public MajorCutoffResponse update(

            UUID id,

            UpdateMajorCutoffRequest request
    ) {

        MajorCutoff cutoff =
                getCutoff(id);

        if (request.cutoffScore() != null) {

            cutoff.setCutoffScore(
                    request.cutoffScore()
            );
        }

        if (request.publishedAt() != null) {

            cutoff.setPublishedAt(
                    request.publishedAt()
            );
        }

        return toResponse(
                majorCutoffRepository.save(
                        cutoff
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public MajorCutoffResponse updateStatus(

            UUID id,

            MajorCutoffStatus status
    ) {

        if (status == null) {

            throw new BusinessException(
                    "MAJOR_CUTOFF_STATUS_REQUIRED",
                    "Trạng thái điểm chuẩn không được để trống"
            );
        }

        MajorCutoff cutoff =
                getCutoff(id);

        cutoff.setStatus(
                status
        );

        return toResponse(
                majorCutoffRepository.save(
                        cutoff
                )
        );
    }


    // =========================================================
    // PUBLISH NOW
    // =========================================================

    @Transactional
    public MajorCutoffResponse publish(
            UUID id
    ) {

        MajorCutoff cutoff =
                getCutoff(id);

        cutoff.setPublishedAt(
                Instant.now()
        );

        cutoff.setStatus(
                MajorCutoffStatus.ACTIVE
        );

        return toResponse(
                majorCutoffRepository.save(
                        cutoff
                )
        );
    }


    // =========================================================
    // DELETE -> INACTIVE
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        MajorCutoff cutoff =
                getCutoff(id);

        cutoff.setStatus(
                MajorCutoffStatus.INACTIVE
        );

        majorCutoffRepository.save(
                cutoff
        );
    }


    // =========================================================
    // INTERNAL
    // =========================================================

    private MajorCutoff getCutoff(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "MAJOR_CUTOFF_ID_REQUIRED",
                    "ID điểm chuẩn không được để trống"
            );
        }

        return majorCutoffRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "MAJOR_CUTOFF_NOT_FOUND",
                                "Không tìm thấy điểm chuẩn"
                        )
                );
    }

    private AdmissionYear getAdmissionYear(
            UUID id
    ) {

        return admissionYearRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "ADMISSION_YEAR_NOT_FOUND",
                                "Không tìm thấy năm tuyển sinh"
                        )
                );
    }

    private AdmissionRound getAdmissionRound(
            UUID id
    ) {

        return admissionRoundRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "ADMISSION_ROUND_NOT_FOUND",
                                "Không tìm thấy đợt tuyển sinh"
                        )
                );
    }

    private Major getMajor(
            UUID id
    ) {

        return majorRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "MAJOR_NOT_FOUND",
                                "Không tìm thấy ngành"
                        )
                );
    }

    private AdmissionMethod getAdmissionMethod(
            UUID id
    ) {

        AdmissionMethod method =
                admissionMethodRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BusinessException(
                                        "ADMISSION_METHOD_NOT_FOUND",
                                        "Không tìm thấy phương thức xét tuyển"
                                )
                        );

        if (method.getStatus()
                != AdmissionMethodStatus.ACTIVE) {

            throw new BusinessException(
                    "ADMISSION_METHOD_NOT_ACTIVE",
                    "Phương thức xét tuyển hiện không hoạt động"
            );
        }

        return method;
    }

    private SubjectCombination getSubjectCombination(
            UUID id
    ) {

        return subjectCombinationRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "SUBJECT_COMBINATION_NOT_FOUND",
                                "Không tìm thấy tổ hợp xét tuyển"
                        )
                );
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateRoundBelongsToYear(

            AdmissionRound round,

            AdmissionYear year
    ) {

        if (!round
                .getAdmissionYear()
                .getId()
                .equals(
                        year.getId()
                )) {

            throw new BusinessException(
                    "ADMISSION_ROUND_YEAR_MISMATCH",
                    "Đợt tuyển sinh không thuộc năm tuyển sinh đã chọn"
            );
        }
    }

    private void validateMethodBelongsToMajorUniversity(

            Major major,

            AdmissionMethod method
    ) {

        UUID majorUniversityId =
                major.getUniversity()
                        .getId();

        UUID methodUniversityId =
                method.getUniversity()
                        .getId();

        if (!majorUniversityId.equals(
                methodUniversityId
        )) {

            throw new BusinessException(
                    "ADMISSION_METHOD_UNIVERSITY_MISMATCH",
                    "Phương thức xét tuyển không thuộc trường của ngành"
            );
        }
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private MajorCutoffResponse toResponse(
            MajorCutoff cutoff
    ) {

        AdmissionRound round =
                cutoff.getAdmissionRound();

        SubjectCombination combo =
                cutoff.getSubjectCombination();

        return new MajorCutoffResponse(

                cutoff.getId(),

                cutoff.getAdmissionYear()
                        .getId(),

                cutoff.getAdmissionYear()
                        .getYear(),

                round == null
                        ? null
                        : round.getId(),

                round == null
                        ? null
                        : round.getName(),

                cutoff.getMajor()
                        .getId(),

                cutoff.getMajor()
                        .getCode(),

                cutoff.getMajor()
                        .getName(),

                cutoff.getAdmissionMethod()
                        .getId(),

                cutoff.getAdmissionMethod()
                        .getCode(),

                cutoff.getAdmissionMethod()
                        .getName(),

                combo == null
                        ? null
                        : combo.getId(),

                combo == null
                        ? null
                        : combo.getCode(),

                combo == null
                        ? null
                        : combo.getName(),

                cutoff.getCutoffScore(),

                cutoff.getPublishedAt(),

                cutoff.getStatus() == null
                        ? null
                        : cutoff.getStatus().name(),

                cutoff.getCreatedAt()
        );
    }


    // =========================================================
    // PAGINATION
    // =========================================================

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


    // =========================================================
    // SORT
    // =========================================================

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