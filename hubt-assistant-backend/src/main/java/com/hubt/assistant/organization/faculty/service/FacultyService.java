package com.hubt.assistant.organization.faculty.service;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.faculty.dto.request.CreateFacultyRequest;
import com.hubt.assistant.organization.faculty.dto.request.UpdateFacultyRequest;

import com.hubt.assistant.organization.faculty.dto.response.FacultyResponse;

import com.hubt.assistant.organization.faculty.entity.Faculty;
import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import com.hubt.assistant.organization.faculty.repository.FacultyRepository;

import com.hubt.assistant.organization.faculty.specification.FacultySpecification;

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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FacultyService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "updatedAt",
                    "code",
                    "name",
                    "deanName",
                    "status"
            );


    private final FacultyRepository
            facultyRepository;

    private final UniversityRepository
            universityRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public FacultyResponse create(
            CreateFacultyRequest request
    ) {

        University university =
                getActiveUniversity(
                        request.universityId()
                );


        String code =
                normalizeCode(
                        request.code()
                );


        if (facultyRepository
                .existsByUniversityIdAndCodeIgnoreCase(
                        university.getId(),
                        code
                )) {

            throw new BusinessException(
                    "FACULTY_CODE_EXISTS",
                    "Mã khoa đã tồn tại trong trường này"
            );
        }


        Faculty faculty =
                new Faculty();


        faculty.setUniversity(
                university
        );


        faculty.setCode(
                code
        );


        faculty.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        faculty.setDescription(
                normalizeNullable(
                        request.description()
                )
        );


        faculty.setDeanName(
                normalizeNullable(
                        request.deanName()
                )
        );


        faculty.setStatus(
                FacultyStatus.ACTIVE
        );


        Faculty saved =
                facultyRepository.save(
                        faculty
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<FacultyResponse>
    getAdminFaculties(

            String keyword,

            UUID universityId,

            FacultyStatus status,

            int page,

            int size,

            String sortBy,

            String sortDirection

    ) {

        validatePagination(
                page,
                size
        );


        Sort sort =
                createSort(
                        sortBy,
                        sortDirection
                );


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );


        Specification<Faculty> specification =
                FacultySpecification
                        .hasKeyword(
                                keyword
                        )
                        .and(
                                FacultySpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                FacultySpecification
                                        .hasStatus(
                                                status
                                        )
                        );


        Page<Faculty> facultyPage =
                facultyRepository
                        .findAll(
                                specification,
                                pageable
                        );


        Page<FacultyResponse> responsePage =
                facultyPage.map(
                        this::toResponse
                );


        return PageResponse.from(
                responsePage
        );
    }


    // =========================================================
    // DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public FacultyResponse getById(
            UUID id
    ) {

        return toResponse(
                getFaculty(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public FacultyResponse update(

            UUID id,

            UpdateFacultyRequest request

    ) {

        Faculty faculty =
                getFaculty(
                        id
                );


        if (request.code()
                != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            boolean exists =
                    facultyRepository
                            .existsByUniversityIdAndCodeIgnoreCaseAndIdNot(
                                    faculty.getUniversity()
                                            .getId(),
                                    code,
                                    id
                            );


            if (exists) {

                throw new BusinessException(
                        "FACULTY_CODE_EXISTS",
                        "Mã khoa đã tồn tại trong trường này"
                );
            }


            faculty.setCode(
                    code
            );
        }


        if (request.name()
                != null) {

            faculty.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        if (request.description()
                != null) {

            faculty.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }


        if (request.deanName()
                != null) {

            faculty.setDeanName(
                    normalizeNullable(
                            request.deanName()
                    )
            );
        }


        Faculty saved =
                facultyRepository.save(
                        faculty
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Transactional
    public FacultyResponse updateStatus(

            UUID id,

            FacultyStatus status

    ) {

        if (status == null) {

            throw new BusinessException(
                    "FACULTY_STATUS_REQUIRED",
                    "Trạng thái khoa không được để trống"
            );
        }


        Faculty faculty =
                getFaculty(
                        id
                );


        if (faculty.getStatus()
                == status) {

            return toResponse(
                    faculty
            );
        }


        faculty.setStatus(
                status
        );


        Faculty saved =
                facultyRepository.save(
                        faculty
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // DELETE
    //
    // DB hiện tại KHÔNG có deleted_at.
    // Vì vậy Phase 3.2 không giả soft-delete.
    // Ta chuyển về INACTIVE.
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        Faculty faculty =
                getFaculty(
                        id
                );


        faculty.setStatus(
                FacultyStatus.INACTIVE
        );


        facultyRepository.save(
                faculty
        );
    }


    // =========================================================
    // PUBLIC LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<FacultyResponse>
    getPublicFaculties(
            UUID universityId
    ) {

        if (universityId != null) {

            getActiveUniversity(
                    universityId
            );


            return facultyRepository
                    .findAllByUniversityIdAndStatusOrderByNameAsc(
                            universityId,
                            FacultyStatus.ACTIVE
                    )
                    .stream()
                    .map(
                            this::toResponse
                    )
                    .toList();
        }


        return facultyRepository
                .findAllByStatusOrderByNameAsc(
                        FacultyStatus.ACTIVE
                )
                .stream()
                .filter(
                        faculty ->
                                faculty.getUniversity()
                                        .getStatus()
                                        == UniversityStatus.ACTIVE
                )
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // PUBLIC DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public FacultyResponse getPublicById(
            UUID id
    ) {

        Faculty faculty =
                getFaculty(
                        id
                );


        if (faculty.getStatus()
                != FacultyStatus.ACTIVE
                ||
                faculty.getUniversity()
                        .getStatus()
                        != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "FACULTY_NOT_FOUND",
                    "Không tìm thấy khoa"
            );
        }


        return toResponse(
                faculty
        );
    }


    // =========================================================
    // GET FACULTY
    // =========================================================

    private Faculty getFaculty(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "FACULTY_ID_REQUIRED",
                    "ID khoa không được để trống"
            );
        }


        return facultyRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "FACULTY_NOT_FOUND",
                                        "Không tìm thấy khoa"
                                )
                );
    }


    // =========================================================
    // GET ACTIVE UNIVERSITY
    // =========================================================

    private University getActiveUniversity(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "UNIVERSITY_ID_REQUIRED",
                    "University ID không được để trống"
            );
        }


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
                    "Trường đại học hiện không hoạt động"
            );
        }


        return university;
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private FacultyResponse toResponse(
            Faculty faculty
    ) {

        University university =
                faculty.getUniversity();


        return new FacultyResponse(

                faculty.getId(),

                university.getId(),

                university.getCode(),

                university.getName(),

                faculty.getCode(),

                faculty.getName(),

                faculty.getDescription(),

                faculty.getDeanName(),

                faculty.getStatus() == null
                        ? null
                        : faculty.getStatus()
                        .name(),

                faculty.getCreatedAt(),

                faculty.getUpdatedAt()
        );
    }


    // =========================================================
    // NORMALIZE CODE
    // =========================================================

    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "FACULTY_CODE_REQUIRED",
                    "Mã khoa không được để trống"
            );
        }


        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }


    // =========================================================
    // NORMALIZE NAME
    // =========================================================

    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "FACULTY_NAME_REQUIRED",
                    "Tên khoa không được để trống"
            );
        }


        return value.trim();
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


        String normalized =
                value.trim();


        return normalized.isBlank()
                ? null
                : normalized;
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


        if (!ALLOWED_SORT_FIELDS
                .contains(
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