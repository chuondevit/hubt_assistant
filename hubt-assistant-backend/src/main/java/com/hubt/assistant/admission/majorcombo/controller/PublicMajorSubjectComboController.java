package com.hubt.assistant.admission.majorcombo.controller;

import com.hubt.assistant.admission.majorcombo.dto.response.MajorSubjectComboResponse;
import com.hubt.assistant.admission.majorcombo.service.MajorSubjectComboService;
import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/major-subject-combos")
@RequiredArgsConstructor
public class PublicMajorSubjectComboController {

    private final MajorSubjectComboService
            majorSubjectComboService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MajorSubjectComboResponse>>
            > getAll(

            @RequestParam
            UUID majorAdmissionMethodId

    ) {

        List<MajorSubjectComboResponse> result =
                majorSubjectComboService
                        .getAll(
                                majorAdmissionMethodId
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách tổ hợp xét tuyển thành công",
                        result
                )
        );
    }
}