package com.hubt.assistant.admission.year.controller;

import com.hubt.assistant.admission.year.dto.request.CreateAdmissionYearRequest;
import com.hubt.assistant.admission.year.dto.request.UpdateAdmissionYearRequest;
import com.hubt.assistant.admission.year.dto.request.UpdateAdmissionYearStatusRequest;

import com.hubt.assistant.admission.year.dto.response.AdmissionYearResponse;

import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;

import com.hubt.assistant.admission.year.service.AdmissionYearService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/admission-years")
@RequiredArgsConstructor
public class AdminAdmissionYearController {

    private final AdmissionYearService
            admissionYearService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<AdmissionYearResponse>
            > create(

            @Valid
            @RequestBody
            CreateAdmissionYearRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo năm tuyển sinh thành công",
                                admissionYearService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<AdmissionYearResponse>
                    >
            > getAdmissionYears(

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            AdmissionYearStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "year")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách năm tuyển sinh thành công",
                        admissionYearService
                                .getAdminAdmissionYears(
                                        universityId,
                                        year,
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
            ApiResponse<AdmissionYearResponse>
            > getById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy năm tuyển sinh thành công",
                        admissionYearService.getById(
                                id
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<AdmissionYearResponse>
            > update(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionYearRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật năm tuyển sinh thành công",
                        admissionYearService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<AdmissionYearResponse>
            > updateStatus(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionYearStatusRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái năm tuyển sinh thành công",
                        admissionYearService.updateStatus(
                                id,
                                request.status()
                        )
                )
        );
    }
}