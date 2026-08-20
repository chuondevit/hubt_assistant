package com.hubt.assistant.admission.method.service;

import com.hubt.assistant.admission.method.dto.request.CreateAdmissionMethodRequest;
import com.hubt.assistant.admission.method.dto.request.UpdateAdmissionMethodRequest;

import com.hubt.assistant.admission.method.dto.response.AdmissionMethodResponse;

import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;

import com.hubt.assistant.admission.method.repository.AdmissionMethodRepository;
import com.hubt.assistant.admission.method.specification.AdmissionMethodSpecification;

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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdmissionMethodService {

    private static final int MAX_PAGE_SIZE = 100;


    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "code",
                    "name",
                    "status",
                    "createdAt",
                    "updatedAt"
            );


    private final AdmissionMethodRepository
            admissionMethodRepository;

    private final UniversityRepository
            universityRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public AdmissionMethodResponse create(
            CreateAdmissionMethodRequest request
    ) {

        University university =
                getActiveUniversity(
                        request.universityId()
                );


        String code =
                normalizeCode(
                        request.code()
                );


        if (admissionMethodRepository
                .existsByUniversity_IdAndCodeIgnoreCase(
                        university.getId(),
                        code
                )) {

            throw new BusinessException(
                    "ADMISSION_METHOD_CODE_EXISTS",
                    "Mã phương thức xét tuyển đã tồn tại trong trường này"
            );
        }


        AdmissionMethod method =
                new AdmissionMethod();


        method.setUniversity(
                university
        );


        method.setCode(
                code
        );


        method.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        method.setDescription(
                normalizeNullable(
                        request.description()
                )
        );


        method.setStatus(
                AdmissionMethodStatus.ACTIVE
        );


        return toResponse(
                admissionMethodRepository.save(
                        method
                )
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<AdmissionMethodResponse>
    getAdminMethods(

            String keyword,

            UUID universityId,

            AdmissionMethodStatus status,

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


        Specification<AdmissionMethod> specification =
                AdmissionMethodSpecification
                        .hasKeyword(keyword)
                        .and(
                                AdmissionMethodSpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                AdmissionMethodSpecification
                                        .hasStatus(
                                                status
                                        )
                        );


        Page<AdmissionMethodResponse> result =
                admissionMethodRepository
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
    public AdmissionMethodResponse getById(
            UUID id
    ) {

        return toResponse(
                getMethod(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public AdmissionMethodResponse update(

            UUID id,

            UpdateAdmissionMethodRequest request

    ) {

        AdmissionMethod method =
                getMethod(id);


        if (request.code() != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            if (admissionMethodRepository
                    .existsByUniversity_IdAndCodeIgnoreCaseAndIdNot(
                            method.getUniversity().getId(),
                            code,
                            id
                    )) {

                throw new BusinessException(
                        "ADMISSION_METHOD_CODE_EXISTS",
                        "Mã phương thức xét tuyển đã tồn tại trong trường này"
                );
            }


            method.setCode(
                    code
            );
        }


        if (request.name() != null) {

            method.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        if (request.description() != null) {

            method.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }


        return toResponse(
                admissionMethodRepository.save(
                        method
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public AdmissionMethodResponse updateStatus(

            UUID id,

            AdmissionMethodStatus status

    ) {

        if (status == null) {

            throw new BusinessException(
                    "ADMISSION_METHOD_STATUS_REQUIRED",
                    "Trạng thái phương thức xét tuyển không được để trống"
            );
        }


        AdmissionMethod method =
                getMethod(id);


        method.setStatus(
                status
        );


        return toResponse(
                admissionMethodRepository.save(
                        method
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

        AdmissionMethod method =
                getMethod(id);


        method.setStatus(
                AdmissionMethodStatus.INACTIVE
        );


        admissionMethodRepository.save(
                method
        );
    }


    // =========================================================
    // PUBLIC
    // =========================================================

    @Transactional(readOnly = true)
    public List<AdmissionMethodResponse>
    getPublicMethods(
            UUID universityId
    ) {

        if (universityId != null) {

            return admissionMethodRepository
                    .findAllByUniversity_IdAndStatusOrderByNameAsc(
                            universityId,
                            AdmissionMethodStatus.ACTIVE
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


        return admissionMethodRepository
                .findAllByStatusOrderByNameAsc(
                        AdmissionMethodStatus.ACTIVE
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

    private AdmissionMethod getMethod(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "ADMISSION_METHOD_ID_REQUIRED",
                    "ID phương thức xét tuyển không được để trống"
            );
        }


        return admissionMethodRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "ADMISSION_METHOD_NOT_FOUND",
                                        "Không tìm thấy phương thức xét tuyển"
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


    private AdmissionMethodResponse toResponse(
            AdmissionMethod method
    ) {

        return new AdmissionMethodResponse(

                method.getId(),

                method.getUniversity().getId(),

                method.getUniversity().getCode(),

                method.getUniversity().getName(),

                method.getCode(),

                method.getName(),

                method.getDescription(),

                method.getStatus() == null
                        ? null
                        : method.getStatus().name(),

                method.getCreatedAt(),

                method.getUpdatedAt()
        );
    }


    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "ADMISSION_METHOD_CODE_REQUIRED",
                    "Mã phương thức xét tuyển không được để trống"
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
                    "ADMISSION_METHOD_NAME_REQUIRED",
                    "Tên phương thức xét tuyển không được để trống"
            );
        }


        return value.trim();
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