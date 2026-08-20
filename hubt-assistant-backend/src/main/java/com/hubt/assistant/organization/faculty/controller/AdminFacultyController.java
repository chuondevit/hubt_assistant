package com.hubt.assistant.organization.faculty.controller;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.organization.faculty.dto.request.CreateFacultyRequest;
import com.hubt.assistant.organization.faculty.dto.request.UpdateFacultyRequest;
import com.hubt.assistant.organization.faculty.dto.request.UpdateFacultyStatusRequest;

import com.hubt.assistant.organization.faculty.dto.response.FacultyResponse;

import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import com.hubt.assistant.organization.faculty.service.FacultyService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/faculties")
@RequiredArgsConstructor
public class AdminFacultyController {

    private final FacultyService
            facultyService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<FacultyResponse>
            > create(

            @Valid
            @RequestBody
            CreateFacultyRequest request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Tạo khoa thành công",
                                facultyService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<FacultyResponse>
                    >
            > getFaculties(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            FacultyStatus status,

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
                        "Lấy danh sách khoa thành công",
                        facultyService
                                .getAdminFaculties(
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
            ApiResponse<FacultyResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin khoa thành công",
                        facultyService
                                .getById(id)
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<FacultyResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateFacultyRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật khoa thành công",
                        facultyService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<FacultyResponse>
            > updateStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateFacultyStatusRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái khoa thành công",
                        facultyService.updateStatus(
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

        facultyService.delete(
                id
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa khoa thành công",
                        null
                )
        );
    }
}