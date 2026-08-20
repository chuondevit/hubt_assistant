package com.hubt.assistant.admission.planmethod.controller;

import com.hubt.assistant.admission.planmethod.dto.request.CreateMajorAdmissionMethodRequest;
import com.hubt.assistant.admission.planmethod.dto.request.UpdateMajorAdmissionMethodRequest;
import com.hubt.assistant.admission.planmethod.dto.request.UpdateMajorAdmissionMethodStatusRequest;

import com.hubt.assistant.admission.planmethod.dto.response.MajorAdmissionMethodResponse;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;

import com.hubt.assistant.admission.planmethod.service.MajorAdmissionMethodService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/major-admission-methods")
@RequiredArgsConstructor
public class AdminMajorAdmissionMethodController {

    private final MajorAdmissionMethodService
            majorAdmissionMethodService;

    @PostMapping
    public ResponseEntity<
            ApiResponse<MajorAdmissionMethodResponse>
            > create(

            @Valid
            @RequestBody
            CreateMajorAdmissionMethodRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Thêm phương thức xét tuyển vào kế hoạch thành công",
                                majorAdmissionMethodService.create(
                                        request
                                )
                        )
                );
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<MajorAdmissionMethodResponse>>
            > getAll(

            @RequestParam(required = false)
            UUID planId,

            @RequestParam(required = false)
            UUID admissionMethodId,

            @RequestParam(required = false)
            MajorAdmissionMethodStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách phương thức xét tuyển ngành thành công",

                        majorAdmissionMethodService
                                .getAdminMethods(
                                        planId,
                                        admissionMethodId,
                                        status,
                                        page,
                                        size,
                                        sortBy,
                                        sortDirection
                                )
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorAdmissionMethodResponse>
            > getById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy phương thức xét tuyển ngành thành công",
                        majorAdmissionMethodService
                                .getById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorAdmissionMethodResponse>
            > update(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateMajorAdmissionMethodRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật phương thức xét tuyển ngành thành công",

                        majorAdmissionMethodService
                                .update(
                                        id,
                                        request
                                )
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<MajorAdmissionMethodResponse>
            > updateStatus(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateMajorAdmissionMethodStatusRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái phương thức xét tuyển thành công",

                        majorAdmissionMethodService
                                .updateStatus(
                                        id,
                                        request.status()
                                )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable UUID id
    ) {

        majorAdmissionMethodService.delete(
                id
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa phương thức xét tuyển thành công",
                        null
                )
        );
    }
}