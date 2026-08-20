package com.hubt.assistant.admission.subject.controller;

import com.hubt.assistant.admission.subject.dto.request.CreateSubjectRequest;
import com.hubt.assistant.admission.subject.dto.request.UpdateSubjectRequest;

import com.hubt.assistant.admission.subject.dto.response.SubjectResponse;

import com.hubt.assistant.admission.subject.service.SubjectService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/subjects")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final SubjectService
            subjectService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<SubjectResponse>
            > create(

            @Valid
            @RequestBody
            CreateSubjectRequest request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Tạo môn học thành công",
                                subjectService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<SubjectResponse>>
            > getSubjects(

            @RequestParam(required = false)
            String keyword,

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
                        "Lấy danh sách môn học thành công",

                        subjectService
                                .getAdminSubjects(
                                        keyword,
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
            ApiResponse<SubjectResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy môn học thành công",
                        subjectService.getById(
                                id
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<SubjectResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateSubjectRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật môn học thành công",

                        subjectService.update(
                                id,
                                request
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

        subjectService.delete(
                id
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa môn học thành công",
                        null
                )
        );
    }
}