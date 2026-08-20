package com.hubt.assistant.admission.combination.controller;

import com.hubt.assistant.admission.combination.dto.request.CreateSubjectCombinationRequest;
import com.hubt.assistant.admission.combination.dto.request.UpdateSubjectCombinationRequest;

import com.hubt.assistant.admission.combination.dto.response.SubjectCombinationResponse;

import com.hubt.assistant.admission.combination.service.SubjectCombinationService;

import com.hubt.assistant.common.api.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/subject-combinations")
@RequiredArgsConstructor
public class AdminSubjectCombinationController {

    private final SubjectCombinationService
            subjectCombinationService;

    @PostMapping
    public ResponseEntity<
            ApiResponse<SubjectCombinationResponse>
            > create(

            @Valid
            @RequestBody
            CreateSubjectCombinationRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo tổ hợp môn thành công",
                                subjectCombinationService
                                        .create(request)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<SubjectCombinationResponse>>
            > getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách tổ hợp môn thành công",
                        subjectCombinationService
                                .getAll()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<SubjectCombinationResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy tổ hợp môn thành công",
                        subjectCombinationService
                                .getById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<SubjectCombinationResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateSubjectCombinationRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật tổ hợp môn thành công",
                        subjectCombinationService
                                .update(
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

        subjectCombinationService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa tổ hợp môn thành công",
                        null
                )
        );
    }
}