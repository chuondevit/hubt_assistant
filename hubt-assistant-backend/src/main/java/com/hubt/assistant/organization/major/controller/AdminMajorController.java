package com.hubt.assistant.organization.major.controller;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.organization.major.dto.request.CreateMajorRequest;
import com.hubt.assistant.organization.major.dto.request.UpdateMajorRequest;
import com.hubt.assistant.organization.major.dto.request.UpdateMajorStatusRequest;

import com.hubt.assistant.organization.major.dto.response.MajorResponse;

import com.hubt.assistant.organization.major.entity.DegreeLevel;
import com.hubt.assistant.organization.major.entity.MajorStatus;

import com.hubt.assistant.organization.major.service.MajorService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/majors")
@RequiredArgsConstructor
public class AdminMajorController {

    private final MajorService
            majorService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<MajorResponse>
            > create(

            @Valid
            @RequestBody
            CreateMajorRequest request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Tạo ngành thành công",
                                majorService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<MajorResponse>
                    >
            > getMajors(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            UUID facultyId,

            @RequestParam(required = false)
            DegreeLevel degreeLevel,

            @RequestParam(required = false)
            MajorStatus status,

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
                        "Lấy danh sách ngành thành công",
                        majorService
                                .getAdminMajors(
                                        keyword,
                                        universityId,
                                        facultyId,
                                        degreeLevel,
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
            ApiResponse<MajorResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin ngành thành công",
                        majorService.getById(
                                id
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateMajorRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật ngành thành công",
                        majorService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<MajorResponse>
            > updateStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateMajorStatusRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái ngành thành công",
                        majorService.updateStatus(
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

        majorService.delete(
                id
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa ngành thành công",
                        null
                )
        );
    }
}