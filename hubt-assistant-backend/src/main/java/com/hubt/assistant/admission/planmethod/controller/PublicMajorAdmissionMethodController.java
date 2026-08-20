package com.hubt.assistant.admission.planmethod.controller;

import com.hubt.assistant.admission.planmethod.dto.response.MajorAdmissionMethodResponse;

import com.hubt.assistant.admission.planmethod.service.MajorAdmissionMethodService;

import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/major-admission-methods")
@RequiredArgsConstructor
public class PublicMajorAdmissionMethodController {

    private final MajorAdmissionMethodService
            majorAdmissionMethodService;

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MajorAdmissionMethodResponse>>
            > getMethods(

            @RequestParam
            UUID planId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy phương thức xét tuyển thành công",

                        majorAdmissionMethodService
                                .getPublicMethods(
                                        planId
                                )
                )
        );
    }
}