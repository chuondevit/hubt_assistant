package com.hubt.assistant.admission.combination.controller;

import com.hubt.assistant.admission.combination.dto.response.SubjectCombinationResponse;
import com.hubt.assistant.admission.combination.service.SubjectCombinationService;
import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/subject-combinations")
@RequiredArgsConstructor
public class PublicSubjectCombinationController {

    private final SubjectCombinationService
            subjectCombinationService;

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
}