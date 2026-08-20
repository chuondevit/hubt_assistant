package com.hubt.assistant.admission.plan.controller;

import com.hubt.assistant.admission.plan.dto.request.CreateMajorAdmissionPlanRequest;
import com.hubt.assistant.admission.plan.dto.request.UpdateApplicationOpenRequest;
import com.hubt.assistant.admission.plan.dto.request.UpdateMajorAdmissionPlanRequest;

import com.hubt.assistant.admission.plan.dto.response.MajorAdmissionPlanResponse;

import com.hubt.assistant.admission.plan.service.MajorAdmissionPlanService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/major-admission-plans")
@RequiredArgsConstructor
public class AdminMajorAdmissionPlanController {

    private final MajorAdmissionPlanService
            majorAdmissionPlanService;


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<MajorAdmissionPlanResponse>
            > create(

            @Valid
            @RequestBody
            CreateMajorAdmissionPlanRequest request

    ) {

        MajorAdmissionPlanResponse result =
                majorAdmissionPlanService
                        .create(
                                request
                        );


        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Tạo kế hoạch tuyển sinh ngành thành công",
                                result
                        )
                );
    }


    // =========================================================
    // GET ALL
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

            @RequestParam(required = false)
            Boolean applicationOpen,

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
                                applicationOpen,
                                pageable
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách kế hoạch tuyển sinh ngành thành công",
                        result
                )
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorAdmissionPlanResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy kế hoạch tuyển sinh ngành thành công",
                        majorAdmissionPlanService
                                .getById(
                                        id
                                )
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorAdmissionPlanResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateMajorAdmissionPlanRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật kế hoạch tuyển sinh ngành thành công",

                        majorAdmissionPlanService
                                .update(
                                        id,
                                        request
                                )
                )
        );
    }


    // =========================================================
    // UPDATE APPLICATION OPEN
    // =========================================================

    @PatchMapping("/{id}/application")
    public ResponseEntity<
            ApiResponse<MajorAdmissionPlanResponse>
            > updateApplicationOpen(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateApplicationOpenRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái nhận hồ sơ thành công",

                        majorAdmissionPlanService
                                .updateApplicationOpen(
                                        id,
                                        request
                                )
                )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Void>
            > delete(

            @PathVariable
            UUID id

    ) {

        majorAdmissionPlanService
                .delete(
                        id
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa kế hoạch tuyển sinh ngành thành công",
                        null
                )
        );
    }
}