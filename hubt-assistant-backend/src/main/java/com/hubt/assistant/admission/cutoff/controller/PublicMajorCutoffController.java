package com.hubt.assistant.admission.cutoff.controller;

import com.hubt.assistant.admission.cutoff.dto.response.MajorCutoffResponse;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import com.hubt.assistant.admission.cutoff.service.MajorCutoffService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/major-cutoffs")
@RequiredArgsConstructor
public class PublicMajorCutoffController {

    private final MajorCutoffService
            majorCutoffService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<MajorCutoffResponse>>
            > getAll(

            @RequestParam(required = false)
            UUID admissionYearId,

            @RequestParam(required = false)
            UUID admissionRoundId,

            @RequestParam(required = false)
            UUID majorId,

            @RequestParam(required = false)
            UUID admissionMethodId,

            @RequestParam(required = false)
            UUID subjectComboId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        PageResponse<MajorCutoffResponse> result =
                majorCutoffService.getAll(

                        admissionYearId,
                        admissionRoundId,
                        majorId,
                        admissionMethodId,
                        subjectComboId,

                        MajorCutoffStatus.ACTIVE,

                        page,
                        size,

                        "publishedAt",
                        "desc"
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách điểm chuẩn thành công",
                        result
                )
        );
    }
}