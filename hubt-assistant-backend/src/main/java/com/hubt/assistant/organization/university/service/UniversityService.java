package com.hubt.assistant.organization.university.service;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.university.dto.request.CreateUniversityRequest;
import com.hubt.assistant.organization.university.dto.request.UpdateUniversityRequest;
import com.hubt.assistant.organization.university.dto.response.UniversityResponse;

import com.hubt.assistant.organization.university.entity.University;
import com.hubt.assistant.organization.university.entity.UniversityStatus;

import com.hubt.assistant.organization.university.repository.UniversityRepository;
import com.hubt.assistant.organization.university.specification.UniversitySpecification;

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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UniversityService {

    // =========================================================
    // CONSTANT
    // =========================================================

    private static final int MAX_PAGE_SIZE = 100;

    /*
     * Chỉ cho phép sort theo các field này.
     *
     * Không nhận trực tiếp field tùy ý từ client
     * để tránh lỗi property không tồn tại.
     */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "updatedAt",
                    "code",
                    "name",
                    "shortName",
                    "status"
            );


    // =========================================================
    // DEPENDENCY
    // =========================================================

    private final UniversityRepository universityRepository;


    // =========================================================
    // CREATE UNIVERSITY
    // =========================================================

    @Transactional
    public UniversityResponse create(
            CreateUniversityRequest request
    ) {

        // ---------------------------------------------
        // Normalize university code
        // ---------------------------------------------

        String code =
                normalizeCode(
                        request.code()
                );


        // ---------------------------------------------
        // Check duplicate code
        // ---------------------------------------------

        boolean codeExists =
                universityRepository
                        .existsByCodeIgnoreCaseAndDeletedAtIsNull(
                                code
                        );

        if (codeExists) {

            throw new BusinessException(
                    "UNIVERSITY_CODE_EXISTS",
                    "Mã trường đại học đã tồn tại"
            );
        }


        // ---------------------------------------------
        // Create entity
        // ---------------------------------------------

        University university =
                new University();


        university.setCode(
                code
        );


        university.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        university.setShortName(
                normalizeNullable(
                        request.shortName()
                )
        );


        university.setDescription(
                normalizeNullable(
                        request.description()
                )
        );


        university.setAddress(
                normalizeNullable(
                        request.address()
                )
        );


        university.setEmail(
                normalizeEmail(
                        request.email()
                )
        );


        university.setPhone(
                normalizeNullable(
                        request.phone()
                )
        );


        university.setWebsite(
                normalizeNullable(
                        request.website()
                )
        );


        university.setLogoUrl(
                normalizeNullable(
                        request.logoUrl()
                )
        );


        university.setStatus(
                UniversityStatus.ACTIVE
        );


        // ---------------------------------------------
        // Save
        // ---------------------------------------------

        University saved =
                universityRepository.save(
                        university
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // ADMIN - GET UNIVERSITY LIST
    // SEARCH + FILTER + PAGINATION + SORT
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<UniversityResponse>
    getAdminUniversities(

            String keyword,

            UniversityStatus status,

            int page,

            int size,

            String sortBy,

            String sortDirection
    ) {

        // ---------------------------------------------
        // Validate pagination
        // ---------------------------------------------

        validatePagination(
                page,
                size
        );


        // ---------------------------------------------
        // Create Sort
        // ---------------------------------------------

        Sort sort =
                createSort(
                        sortBy,
                        sortDirection
                );


        // ---------------------------------------------
        // Create Pageable
        // ---------------------------------------------

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );


        // ---------------------------------------------
        // Build Specification
        // ---------------------------------------------

        Specification<University> specification =
        UniversitySpecification
                .notDeleted()
                .and(
                        UniversitySpecification.hasKeyword(
                                keyword
                        )
                )
                .and(
                        UniversitySpecification.hasStatus(
                                status
                        )
                );

        // ---------------------------------------------
        // Query database
        // ---------------------------------------------

        Page<University> universityPage =
                universityRepository.findAll(
                        specification,
                        pageable
                );


        // ---------------------------------------------
        // Entity -> DTO
        // ---------------------------------------------

        Page<UniversityResponse> responsePage =
                universityPage.map(
                        this::toResponse
                );


        // ---------------------------------------------
        // Convert to project PageResponse
        // ---------------------------------------------

        return PageResponse.from(
                responsePage
        );
    }


    // =========================================================
    // ADMIN - GET UNIVERSITY DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public UniversityResponse getById(
            UUID id
    ) {

        University university =
                getExistingUniversity(
                        id
                );


        return toResponse(
                university
        );
    }


    // =========================================================
    // ADMIN - UPDATE UNIVERSITY
    // =========================================================

    @Transactional
    public UniversityResponse update(

            UUID id,

            UpdateUniversityRequest request
    ) {

        // ---------------------------------------------
        // Find university
        // ---------------------------------------------

        University university =
                getExistingUniversity(
                        id
                );


        // =====================================================
        // CODE
        // =====================================================

        if (request.code() != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            boolean codeExists =
                    universityRepository
                            .existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(
                                    code,
                                    id
                            );


            if (codeExists) {

                throw new BusinessException(
                        "UNIVERSITY_CODE_EXISTS",
                        "Mã trường đại học đã tồn tại"
                );
            }


            /*
             * HUBT là university mặc định đang được Auth/Register
             * sử dụng.
             *
             * Không cho đổi code HUBT ở Phase 3.
             */
            if ("HUBT".equalsIgnoreCase(
                    university.getCode()
            )
                    &&
                    !"HUBT".equalsIgnoreCase(
                            code
                    )) {

                throw new BusinessException(
                        "DEFAULT_UNIVERSITY_CODE_CANNOT_CHANGE",
                        "Không thể thay đổi mã của trường HUBT mặc định"
                );
            }


            university.setCode(
                    code
            );
        }


        // =====================================================
        // NAME
        // =====================================================

        if (request.name() != null) {

            university.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        // =====================================================
        // SHORT NAME
        // =====================================================

        if (request.shortName() != null) {

            university.setShortName(
                    normalizeNullable(
                            request.shortName()
                    )
            );
        }


        // =====================================================
        // DESCRIPTION
        // =====================================================

        if (request.description() != null) {

            university.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }


        // =====================================================
        // ADDRESS
        // =====================================================

        if (request.address() != null) {

            university.setAddress(
                    normalizeNullable(
                            request.address()
                    )
            );
        }


        // =====================================================
        // EMAIL
        // =====================================================

        if (request.email() != null) {

            university.setEmail(
                    normalizeEmail(
                            request.email()
                    )
            );
        }


        // =====================================================
        // PHONE
        // =====================================================

        if (request.phone() != null) {

            university.setPhone(
                    normalizeNullable(
                            request.phone()
                    )
            );
        }


        // =====================================================
        // WEBSITE
        // =====================================================

        if (request.website() != null) {

            university.setWebsite(
                    normalizeNullable(
                            request.website()
                    )
            );
        }


        // =====================================================
        // LOGO URL
        // =====================================================

        if (request.logoUrl() != null) {

            university.setLogoUrl(
                    normalizeNullable(
                            request.logoUrl()
                    )
            );
        }


        // ---------------------------------------------
        // Save
        // ---------------------------------------------

        University saved =
                universityRepository.save(
                        university
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // ADMIN - UPDATE UNIVERSITY STATUS
    // =========================================================

    @Transactional
    public UniversityResponse updateStatus(

            UUID id,

            UniversityStatus status
    ) {

        // ---------------------------------------------
        // Validate status
        // ---------------------------------------------

        if (status == null) {

            throw new BusinessException(
                    "UNIVERSITY_STATUS_REQUIRED",
                    "Trạng thái trường đại học không được để trống"
            );
        }


        // ---------------------------------------------
        // Find university
        // ---------------------------------------------

        University university =
                getExistingUniversity(
                        id
                );


        /*
         * HUBT hiện đang là university mặc định
         * được Auth/Register sử dụng.
         *
         * Nếu disable HUBT:
         *
         * Register có thể bị ảnh hưởng.
         */
        if ("HUBT".equalsIgnoreCase(
                university.getCode()
        )
                &&
                status == UniversityStatus.INACTIVE) {

            throw new BusinessException(
                    "DEFAULT_UNIVERSITY_CANNOT_BE_DISABLED",
                    "Không thể vô hiệu hóa trường HUBT mặc định"
            );
        }


        // ---------------------------------------------
        // Nothing changed
        // ---------------------------------------------

        if (university.getStatus() == status) {

            return toResponse(
                    university
            );
        }


        university.setStatus(
                status
        );


        University saved =
                universityRepository.save(
                        university
                );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // ADMIN - SOFT DELETE UNIVERSITY
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        University university =
                getExistingUniversity(
                        id
                );


        /*
         * Không cho xóa HUBT.
         *
         * Vì Auth/Register hiện tại đang phụ thuộc
         * university mặc định này.
         */
        if ("HUBT".equalsIgnoreCase(
                university.getCode()
        )) {

            throw new BusinessException(
                    "DEFAULT_UNIVERSITY_CANNOT_BE_DELETED",
                    "Không thể xóa trường HUBT mặc định"
            );
        }


        // ---------------------------------------------
        // Soft delete
        // ---------------------------------------------

        university.setDeletedAt(
                Instant.now()
        );


        universityRepository.save(
                university
        );
    }


    // =========================================================
    // PUBLIC - GET ACTIVE UNIVERSITIES
    // =========================================================

    @Transactional(readOnly = true)
    public List<UniversityResponse>
    getPublicUniversities() {

        return universityRepository
                .findAllByStatusAndDeletedAtIsNullOrderByNameAsc(
                        UniversityStatus.ACTIVE
                )
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // PUBLIC - GET UNIVERSITY BY CODE
    // =========================================================

    @Transactional(readOnly = true)
    public UniversityResponse getPublicByCode(
            String code
    ) {

        // ---------------------------------------------
        // Normalize code
        // ---------------------------------------------

        String normalizedCode =
                normalizeCode(
                        code
                );


        // ---------------------------------------------
        // Find university
        // ---------------------------------------------

        University university =
                universityRepository
                        .findByCodeAndDeletedAtIsNull(
                                normalizedCode
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "UNIVERSITY_NOT_FOUND",
                                                "Không tìm thấy trường đại học"
                                        )
                        );


        // ---------------------------------------------
        // Public only ACTIVE
        // ---------------------------------------------

        if (university.getStatus()
                != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "UNIVERSITY_NOT_FOUND",
                    "Không tìm thấy trường đại học"
            );
        }


        return toResponse(
                university
        );
    }


    // =========================================================
    // INTERNAL - FIND EXISTING UNIVERSITY
    // =========================================================

    private University getExistingUniversity(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "UNIVERSITY_ID_REQUIRED",
                    "ID trường đại học không được để trống"
            );
        }


        return universityRepository
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
    }


    // =========================================================
    // INTERNAL - ENTITY -> RESPONSE
    // =========================================================

    private UniversityResponse toResponse(
            University university
    ) {

        return new UniversityResponse(

                university.getId(),

                university.getCode(),

                university.getName(),

                university.getShortName(),

                university.getDescription(),

                university.getAddress(),

                university.getEmail(),

                university.getPhone(),

                university.getWebsite(),

                university.getLogoUrl(),

                university.getStatus() == null
                        ? null
                        : university
                                .getStatus()
                                .name(),

                university.getCreatedAt(),

                university.getUpdatedAt()
        );
    }


    // =========================================================
    // INTERNAL - NORMALIZE UNIVERSITY CODE
    // =========================================================

    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "UNIVERSITY_CODE_REQUIRED",
                    "Mã trường đại học không được để trống"
            );
        }


        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }


    // =========================================================
    // INTERNAL - NORMALIZE REQUIRED NAME
    // =========================================================

    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "UNIVERSITY_NAME_REQUIRED",
                    "Tên trường đại học không được để trống"
            );
        }


        return value.trim();
    }


    // =========================================================
    // INTERNAL - NORMALIZE NULLABLE STRING
    // =========================================================

    private String normalizeNullable(
            String value
    ) {

        if (value == null) {

            return null;
        }


        String normalized =
                value.trim();


        if (normalized.isEmpty()) {

            return null;
        }


        return normalized;
    }


    // =========================================================
    // INTERNAL - NORMALIZE EMAIL
    // =========================================================

    private String normalizeEmail(
            String value
    ) {

        String email =
                normalizeNullable(
                        value
                );


        if (email == null) {

            return null;
        }


        return email.toLowerCase(
                Locale.ROOT
        );
    }


    // =========================================================
    // INTERNAL - VALIDATE PAGINATION
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
                    "Size phải nằm trong khoảng từ 1 đến 100"
            );
        }
    }


    // =========================================================
    // INTERNAL - CREATE SORT
    // =========================================================

    private Sort createSort(

            String sortBy,

            String sortDirection
    ) {

        // ---------------------------------------------
        // Default field
        // ---------------------------------------------

        String field;

        if (sortBy == null
                || sortBy.isBlank()) {

            field = "createdAt";

        } else {

            field = sortBy.trim();
        }


        // ---------------------------------------------
        // Whitelist
        // ---------------------------------------------

        if (!ALLOWED_SORT_FIELDS.contains(
                field
        )) {

            throw new BusinessException(
                    "INVALID_SORT_FIELD",
                    "Trường sắp xếp không hợp lệ"
            );
        }


        // ---------------------------------------------
        // Direction
        // ---------------------------------------------

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