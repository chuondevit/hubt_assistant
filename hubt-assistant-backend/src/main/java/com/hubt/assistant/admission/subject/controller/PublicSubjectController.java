package com.hubt.assistant.admission.subject.controller;

import com.hubt.assistant.admission.subject.dto.response.SubjectResponse;

import com.hubt.assistant.admission.subject.service.SubjectService;

import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/public/subjects")
@RequiredArgsConstructor
public class PublicSubjectController {

    private final SubjectService
            subjectService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<SubjectResponse>>
            > getSubjects() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách môn học thành công",
                        subjectService
                                .getPublicSubjects()
                )
        );
    }
}