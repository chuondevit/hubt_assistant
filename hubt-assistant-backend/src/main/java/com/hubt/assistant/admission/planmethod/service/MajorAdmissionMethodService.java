package com.hubt.assistant.admission.planmethod.service;

import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;
import com.hubt.assistant.admission.method.repository.AdmissionMethodRepository;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;
import com.hubt.assistant.admission.plan.repository.MajorAdmissionPlanRepository;

import com.hubt.assistant.admission.planmethod.dto.request.CreateMajorAdmissionMethodRequest;
import com.hubt.assistant.admission.planmethod.dto.request.UpdateMajorAdmissionMethodRequest;

import com.hubt.assistant.admission.planmethod.dto.response.MajorAdmissionMethodResponse;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;

import com.hubt.assistant.admission.planmethod.repository.MajorAdmissionMethodRepository;
import com.hubt.assistant.admission.planmethod.specification.MajorAdmissionMethodSpecification;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MajorAdmissionMethodService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "updatedAt",
                    "quota",
                    "minimumScore",
                    "status"
            );

    private final MajorAdmissionMethodRepository
            majorAdmissionMethodRepository;

    private final MajorAdmissionPlanRepository
            majorAdmissionPlanRepository;

    private final AdmissionMethodRepository
            admissionMethodRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public MajorAdmissionMethodResponse create(
            CreateMajorAdmissionMethodRequest request
    ) {

        MajorAdmissionPlan plan =
                getPlan(
                        request.majorAdmissionPlanId()
                );

        AdmissionMethod method =
                getActiveMethod(
                        request.admissionMethodId()
                );

        validateSameUniversity(
                plan,
                method
        );

        if (majorAdmissionMethodRepository
                .existsByMajorAdmissionPlan_IdAndAdmissionMethod_Id(
                        plan.getId(),
                        method.getId()
                )) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_METHOD_EXISTS",
                    "Phương thức xét tuyển đã tồn tại trong kế hoạch này"
            );
        }

        if (request.quota() != null
                && plan.getTotalQuota() != null
                && request.quota() > plan.getTotalQuota()) {

            throw new BusinessException(
                    "ADMISSION_METHOD_QUOTA_INVALID",
                    "Chỉ tiêu của phương thức không được lớn hơn tổng chỉ tiêu"
            );
        }

        MajorAdmissionMethod entity =
                new MajorAdmissionMethod();

        entity.setMajorAdmissionPlan(
                plan
        );

        entity.setAdmissionMethod(
                method
        );

        entity.setQuota(
                request.quota()
        );

        entity.setMinimumScore(
                request.minimumScore()
        );

        entity.setConditionsJson(
                request.conditionsJson() == null
                        ? new HashMap<>()
                        : new HashMap<>(
                                request.conditionsJson()
                        )
        );

        entity.setStatus(
                MajorAdmissionMethodStatus.ACTIVE
        );

        return toResponse(
                majorAdmissionMethodRepository.save(
                        entity
                )
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<MajorAdmissionMethodResponse>
    getAdminMethods(

            UUID planId,

            UUID admissionMethodId,

            MajorAdmissionMethodStatus status,

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

        Specification<MajorAdmissionMethod> specification =
                MajorAdmissionMethodSpecification
                        .hasPlanId(
                                planId
                        )
                        .and(
                                MajorAdmissionMethodSpecification
                                        .hasAdmissionMethodId(
                                                admissionMethodId
                                        )
                        )
                        .and(
                                MajorAdmissionMethodSpecification
                                        .hasStatus(
                                                status
                                        )
                        );

        Page<MajorAdmissionMethodResponse> result =
                majorAdmissionMethodRepository
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
    public MajorAdmissionMethodResponse getById(
            UUID id
    ) {

        return toResponse(
                getEntity(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public MajorAdmissionMethodResponse update(

            UUID id,

            UpdateMajorAdmissionMethodRequest request
    ) {

        MajorAdmissionMethod entity =
                getEntity(
                        id
                );

        if (request.quota() != null) {

            Integer totalQuota =
                    entity
                            .getMajorAdmissionPlan()
                            .getTotalQuota();

            if (totalQuota != null
                    && request.quota() > totalQuota) {

                throw new BusinessException(
                        "ADMISSION_METHOD_QUOTA_INVALID",
                        "Chỉ tiêu của phương thức không được lớn hơn tổng chỉ tiêu"
                );
            }

            entity.setQuota(
                    request.quota()
            );
        }

        if (request.minimumScore() != null) {

            entity.setMinimumScore(
                    request.minimumScore()
            );
        }

        if (request.conditionsJson() != null) {

            entity.setConditionsJson(
                    new HashMap<>(
                            request.conditionsJson()
                    )
            );
        }

        return toResponse(
                majorAdmissionMethodRepository.save(
                        entity
                )
        );
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Transactional
    public MajorAdmissionMethodResponse updateStatus(

            UUID id,

            MajorAdmissionMethodStatus status
    ) {

        if (status == null) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_METHOD_STATUS_REQUIRED",
                    "Trạng thái không được để trống"
            );
        }

        MajorAdmissionMethod entity =
                getEntity(
                        id
                );

        entity.setStatus(
                status
        );

        return toResponse(
                majorAdmissionMethodRepository.save(
                        entity
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

        MajorAdmissionMethod entity =
                getEntity(
                        id
                );

        entity.setStatus(
                MajorAdmissionMethodStatus.INACTIVE
        );

        majorAdmissionMethodRepository.save(
                entity
        );
    }


    // =========================================================
    // PUBLIC
    // =========================================================

    @Transactional(readOnly = true)
    public List<MajorAdmissionMethodResponse>
    getPublicMethods(
            UUID planId
    ) {

        if (planId == null) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_PLAN_ID_REQUIRED",
                    "Major Admission Plan ID không được để trống"
            );
        }

        MajorAdmissionPlan plan =
                getPlan(
                        planId
                );

        if (!Boolean.TRUE.equals(
                plan.getApplicationOpen()
        )) {
            return List.of();
        }

        return majorAdmissionMethodRepository
                .findAllByMajorAdmissionPlan_IdAndStatusOrderByAdmissionMethod_NameAsc(
                        planId,
                        MajorAdmissionMethodStatus.ACTIVE
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

    private MajorAdmissionMethod getEntity(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_METHOD_ID_REQUIRED",
                    "ID phương thức xét tuyển ngành không được để trống"
            );
        }

        return majorAdmissionMethodRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "MAJOR_ADMISSION_METHOD_NOT_FOUND",
                                        "Không tìm thấy phương thức xét tuyển của ngành"
                                )
                );
    }

    private MajorAdmissionPlan getPlan(
            UUID id
    ) {

        return majorAdmissionPlanRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "MAJOR_ADMISSION_PLAN_NOT_FOUND",
                                        "Không tìm thấy kế hoạch tuyển sinh ngành"
                                )
                );
    }

    private AdmissionMethod getActiveMethod(
            UUID id
    ) {

        AdmissionMethod method =
                admissionMethodRepository
                        .findById(
                                id
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "ADMISSION_METHOD_NOT_FOUND",
                                                "Không tìm thấy phương thức xét tuyển"
                                        )
                        );

        if (method.getStatus()
                != AdmissionMethodStatus.ACTIVE) {

            throw new BusinessException(
                    "ADMISSION_METHOD_NOT_ACTIVE",
                    "Phương thức xét tuyển không hoạt động"
            );
        }

        return method;
    }


    // =========================================================
    // VALIDATE UNIVERSITY
    // =========================================================

    private void validateSameUniversity(

            MajorAdmissionPlan plan,

            AdmissionMethod method
    ) {

        UUID planUniversityId =
                plan.getMajor()
                        .getUniversity()
                        .getId();

        UUID methodUniversityId =
                method.getUniversity()
                        .getId();

        if (!planUniversityId.equals(
                methodUniversityId
        )) {

            throw new BusinessException(
                    "ADMISSION_METHOD_UNIVERSITY_MISMATCH",
                    "Phương thức xét tuyển không thuộc trường của kế hoạch tuyển sinh"
            );
        }
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private MajorAdmissionMethodResponse toResponse(
            MajorAdmissionMethod entity
    ) {

        MajorAdmissionPlan plan =
                entity.getMajorAdmissionPlan();

        AdmissionMethod method =
                entity.getAdmissionMethod();

        Map<String, Object> conditions =
                entity.getConditionsJson() == null
                        ? Map.of()
                        : entity.getConditionsJson();

        return new MajorAdmissionMethodResponse(

                entity.getId(),

                plan.getId(),

                plan.getAdmissionYear()
                        .getId(),

                plan.getAdmissionYear()
                        .getYear(),

                plan.getMajor()
                        .getId(),

                plan.getMajor()
                        .getCode(),

                plan.getMajor()
                        .getName(),

                method.getId(),

                method.getCode(),

                method.getName(),

                entity.getQuota(),

                entity.getMinimumScore(),

                conditions,

                entity.getStatus() == null
                        ? null
                        : entity.getStatus().name(),

                entity.getCreatedAt(),

                entity.getUpdatedAt()
        );
    }


    // =========================================================
    // PAGE
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