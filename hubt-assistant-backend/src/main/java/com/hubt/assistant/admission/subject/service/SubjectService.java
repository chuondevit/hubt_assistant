package com.hubt.assistant.admission.subject.service;

import com.hubt.assistant.admission.subject.dto.request.CreateSubjectRequest;
import com.hubt.assistant.admission.subject.dto.request.UpdateSubjectRequest;

import com.hubt.assistant.admission.subject.dto.response.SubjectResponse;

import com.hubt.assistant.admission.subject.entity.Subject;

import com.hubt.assistant.admission.subject.repository.SubjectRepository;

import com.hubt.assistant.admission.subject.specification.SubjectSpecification;

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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SubjectService {

    private static final int MAX_PAGE_SIZE = 100;


    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "code",
                    "name"
            );


    private final SubjectRepository
            subjectRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public SubjectResponse create(
            CreateSubjectRequest request
    ) {

        String code =
                normalizeCode(
                        request.code()
                );


        if (subjectRepository
                .existsByCodeIgnoreCase(
                        code
                )) {

            throw new BusinessException(
                    "SUBJECT_CODE_EXISTS",
                    "Mã môn học đã tồn tại"
            );
        }


        Subject subject =
                new Subject();


        subject.setCode(
                code
        );


        subject.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        return toResponse(
                subjectRepository.save(
                        subject
                )
        );
    }


    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<SubjectResponse>
    getAdminSubjects(

            String keyword,

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


        Specification<Subject> specification =
                SubjectSpecification
                        .hasKeyword(
                                keyword
                        );


        Page<SubjectResponse> result =
                subjectRepository
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
    public SubjectResponse getById(
            UUID id
    ) {

        return toResponse(
                getSubject(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public SubjectResponse update(

            UUID id,

            UpdateSubjectRequest request

    ) {

        Subject subject =
                getSubject(
                        id
                );


        if (request.code()
                != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            if (subjectRepository
                    .existsByCodeIgnoreCaseAndIdNot(
                            code,
                            id
                    )) {

                throw new BusinessException(
                        "SUBJECT_CODE_EXISTS",
                        "Mã môn học đã tồn tại"
                );
            }


            subject.setCode(
                    code
            );
        }


        if (request.name()
                != null) {

            subject.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        return toResponse(
                subjectRepository.save(
                        subject
                )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        Subject subject =
                getSubject(
                        id
                );


        subjectRepository.delete(
                subject
        );
    }


    // =========================================================
    // PUBLIC
    // =========================================================

    @Transactional(readOnly = true)
    public List<SubjectResponse>
    getPublicSubjects() {

        return subjectRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // INTERNAL
    // =========================================================

    private Subject getSubject(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "SUBJECT_ID_REQUIRED",
                    "ID môn học không được để trống"
            );
        }


        return subjectRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "SUBJECT_NOT_FOUND",
                                        "Không tìm thấy môn học"
                                )
                );
    }


    private SubjectResponse toResponse(
            Subject subject
    ) {

        return new SubjectResponse(

                subject.getId(),

                subject.getCode(),

                subject.getName()
        );
    }


    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "SUBJECT_CODE_REQUIRED",
                    "Mã môn học không được để trống"
            );
        }


        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }


    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "SUBJECT_NAME_REQUIRED",
                    "Tên môn học không được để trống"
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
                        ? "name"
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