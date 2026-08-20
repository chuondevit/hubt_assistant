package com.hubt.assistant.organization.program.controller;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.organization.program.dto.request.CreateProgramRequest;
import com.hubt.assistant.organization.program.dto.request.UpdateProgramRequest;
import com.hubt.assistant.organization.program.dto.request.UpdateProgramStatusRequest;

import com.hubt.assistant.organization.program.dto.response.ProgramResponse;

import com.hubt.assistant.organization.program.entity.ProgramStatus;

import com.hubt.assistant.organization.program.service.ProgramService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/programs")
@RequiredArgsConstructor
public class AdminProgramController {

    private final ProgramService programService;


    @PostMapping
    public ResponseEntity<ApiResponse<ProgramResponse>>
    create(
            @Valid
            @RequestBody
            CreateProgramRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo chương trình đào tạo thành công",
                                programService.create(request)
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<ProgramResponse>>
            >
    getPrograms(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            UUID facultyId,

            @RequestParam(required = false)
            UUID majorId,

            @RequestParam(required = false)
            ProgramStatus status,

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
                        "Lấy danh sách chương trình đào tạo thành công",
                        programService.getAdminPrograms(
                                keyword,
                                universityId,
                                facultyId,
                                majorId,
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
    public ResponseEntity<ApiResponse<ProgramResponse>>
    getById(
            @PathVariable
            UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy chương trình đào tạo thành công",
                        programService.getById(id)
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgramResponse>>
    update(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateProgramRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật chương trình đào tạo thành công",
                        programService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProgramResponse>>
    updateStatus(
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateProgramStatusRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái chương trình thành công",
                        programService.updateStatus(
                                id,
                                request.status()
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

        programService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa chương trình thành công",
                        null
                )
        );
    }
}