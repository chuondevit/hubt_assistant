package com.hubt.assistant.admission.plan.controller;

import com.hubt.assistant.admission.plan.dto.response.MajorAdmissionPlanResponse;

import com.hubt.assistant.admission.plan.service.MajorAdmissionPlanService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/major-admission-plans")
@RequiredArgsConstructor
public class PublicMajorAdmissionPlanController {

    private final MajorAdmissionPlanService
            majorAdmissionPlanService;


    // =========================================================
    // PUBLIC LIST
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<MajorAdmissionPlanResponse>
                    >
            > getAll(

            @RequestParam(required = false)
            UUID admissionYearId,

            @RequestParam(required = false)
            UUID majorId,

            @RequestParam(required = false)
            UUID programId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );


        PageResponse<MajorAdmissionPlanResponse> result =
                majorAdmissionPlanService
                        .getAll(
                                admissionYearId,
                                majorId,
                                programId,

                                // Public chỉ thấy plan
                                // đang nhận hồ sơ
                                true,

                                pageable
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách kế hoạch tuyển sinh thành công",
                        result
                )
        );
    }


    // =========================================================
    // PUBLIC DETAIL
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorAdmissionPlanResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        MajorAdmissionPlanResponse result =
                majorAdmissionPlanService
                        .getById(
                                id
                        );


        if (!Boolean.TRUE.equals(
                result.applicationOpen()
        )) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_PLAN_NOT_AVAILABLE",
                    "Kế hoạch tuyển sinh hiện không nhận hồ sơ"
            );
        }


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy kế hoạch tuyển sinh thành công",
                        result
                )
        );
    }
}