package com.hubt.assistant.organization.university.controller;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.organization.university.dto.request.CreateUniversityRequest;
import com.hubt.assistant.organization.university.dto.request.UpdateUniversityRequest;
import com.hubt.assistant.organization.university.dto.request.UpdateUniversityStatusRequest;

import com.hubt.assistant.organization.university.dto.response.UniversityResponse;

import com.hubt.assistant.organization.university.entity.UniversityStatus;

import com.hubt.assistant.organization.university.service.UniversityService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/universities")
@RequiredArgsConstructor
public class AdminUniversityController {

    private final UniversityService universityService;


    // =========================================================
    // CREATE UNIVERSITY
    // =========================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<UniversityResponse>
            > createUniversity(

            @Valid
            @RequestBody
            CreateUniversityRequest request

    ) {

        UniversityResponse result =
                universityService.create(
                        request
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo trường đại học thành công",
                                result
                        )
                );
    }


    // =========================================================
    // GET UNIVERSITY LIST
    // SEARCH + FILTER + PAGINATION + SORT
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<UniversityResponse>
                    >
            > getUniversities(

            @RequestParam(
                    required = false
            )
            String keyword,


            @RequestParam(
                    required = false
            )
            UniversityStatus status,


            @RequestParam(
                    defaultValue = "0"
            )
            int page,


            @RequestParam(
                    defaultValue = "20"
            )
            int size,


            @RequestParam(
                    defaultValue = "createdAt"
            )
            String sortBy,


            @RequestParam(
                    defaultValue = "desc"
            )
            String sortDirection

    ) {

        PageResponse<UniversityResponse> result =
                universityService
                        .getAdminUniversities(
                                keyword,
                                status,
                                page,
                                size,
                                sortBy,
                                sortDirection
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách trường đại học thành công",
                        result
                )
        );
    }


    // =========================================================
    // GET UNIVERSITY DETAIL
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<UniversityResponse>
            > getUniversityById(

            @PathVariable
            UUID id

    ) {

        UniversityResponse result =
                universityService
                        .getById(
                                id
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin trường đại học thành công",
                        result
                )
        );
    }


    // =========================================================
    // UPDATE UNIVERSITY
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<UniversityResponse>
            > updateUniversity(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateUniversityRequest request

    ) {

        UniversityResponse result =
                universityService
                        .update(
                                id,
                                request
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trường đại học thành công",
                        result
                )
        );
    }


    // =========================================================
    // UPDATE UNIVERSITY STATUS
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<UniversityResponse>
            > updateUniversityStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateUniversityStatusRequest request

    ) {

        UniversityResponse result =
                universityService
                        .updateStatus(
                                id,
                                request.status()
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái trường đại học thành công",
                        result
                )
        );
    }


    // =========================================================
    // SOFT DELETE UNIVERSITY
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Void>
            > deleteUniversity(

            @PathVariable
            UUID id

    ) {

        universityService.delete(
                id
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa trường đại học thành công",
                        null
                )
        );
    }
}