package com.hubt.assistant.admission.cutoff.controller;

import com.hubt.assistant.admission.cutoff.dto.request.CreateMajorCutoffRequest;
import com.hubt.assistant.admission.cutoff.dto.request.UpdateMajorCutoffRequest;
import com.hubt.assistant.admission.cutoff.dto.request.UpdateMajorCutoffStatusRequest;

import com.hubt.assistant.admission.cutoff.dto.response.MajorCutoffResponse;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import com.hubt.assistant.admission.cutoff.service.MajorCutoffService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/major-cutoffs")
@RequiredArgsConstructor
public class AdminMajorCutoffController {

    private final MajorCutoffService
            majorCutoffService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<MajorCutoffResponse>
            > create(

            @Valid
            @RequestBody
            CreateMajorCutoffRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo điểm chuẩn thành công",
                                majorCutoffService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<MajorCutoffResponse>>
            > getAll(

            @RequestParam(required = false)
            UUID admissionYearId,

            @RequestParam(required = false)
            UUID admissionRoundId,

            @RequestParam(required = false)
            UUID majorId,

            @RequestParam(required = false)
            UUID admissionMethodId,

            @RequestParam(required = false)
            UUID subjectComboId,

            @RequestParam(required = false)
            MajorCutoffStatus status,

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
                        "Lấy danh sách điểm chuẩn thành công",

                        majorCutoffService.getAll(
                                admissionYearId,
                                admissionRoundId,
                                majorId,
                                admissionMethodId,
                                subjectComboId,
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
            ApiResponse<MajorCutoffResponse>
            > getById(

            @PathVariable
            UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy điểm chuẩn thành công",
                        majorCutoffService.getById(
                                id
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorCutoffResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateMajorCutoffRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật điểm chuẩn thành công",

                        majorCutoffService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<MajorCutoffResponse>
            > updateStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateMajorCutoffStatusRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái điểm chuẩn thành công",

                        majorCutoffService.updateStatus(
                                id,
                                request.status()
                        )
                )
        );
    }


    @PatchMapping("/{id}/publish")
    public ResponseEntity<
            ApiResponse<MajorCutoffResponse>
            > publish(

            @PathVariable
            UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Công bố điểm chuẩn thành công",
                        majorCutoffService.publish(
                                id
                        )
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    delete(

            @PathVariable
            UUID id
    ) {

        majorCutoffService.delete(
                id
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa điểm chuẩn thành công",
                        null
                )
        );
    }
}