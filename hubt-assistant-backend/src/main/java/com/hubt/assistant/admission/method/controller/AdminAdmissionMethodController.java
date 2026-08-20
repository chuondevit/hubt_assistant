package com.hubt.assistant.admission.method.controller;

import com.hubt.assistant.admission.method.dto.request.CreateAdmissionMethodRequest;
import com.hubt.assistant.admission.method.dto.request.UpdateAdmissionMethodRequest;
import com.hubt.assistant.admission.method.dto.request.UpdateAdmissionMethodStatusRequest;

import com.hubt.assistant.admission.method.dto.response.AdmissionMethodResponse;

import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;

import com.hubt.assistant.admission.method.service.AdmissionMethodService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/admission-methods")
@RequiredArgsConstructor
public class AdminAdmissionMethodController {

    private final AdmissionMethodService
            admissionMethodService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<AdmissionMethodResponse>
            > create(

            @Valid
            @RequestBody
            CreateAdmissionMethodRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo phương thức xét tuyển thành công",
                                admissionMethodService
                                        .create(request)
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<AdmissionMethodResponse>>
            > getMethods(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            AdmissionMethodStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "name")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách phương thức xét tuyển thành công",

                        admissionMethodService
                                .getAdminMethods(
                                        keyword,
                                        universityId,
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
            ApiResponse<AdmissionMethodResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy phương thức xét tuyển thành công",

                        admissionMethodService
                                .getById(id)
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<AdmissionMethodResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionMethodRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật phương thức xét tuyển thành công",

                        admissionMethodService
                                .update(
                                        id,
                                        request
                                )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<AdmissionMethodResponse>
            > updateStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionMethodStatusRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái phương thức xét tuyển thành công",

                        admissionMethodService
                                .updateStatus(
                                        id,
                                        request.status()
                                )
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Void>
            > delete(

            @PathVariable
            UUID id

    ) {

        admissionMethodService.delete(
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