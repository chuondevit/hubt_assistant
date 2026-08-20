package com.hubt.assistant.admission.method.controller;

import com.hubt.assistant.admission.method.dto.response.AdmissionMethodResponse;

import com.hubt.assistant.admission.method.service.AdmissionMethodService;

import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/admission-methods")
@RequiredArgsConstructor
public class PublicAdmissionMethodController {

    private final AdmissionMethodService
            admissionMethodService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<AdmissionMethodResponse>>
            > getMethods(

            @RequestParam(required = false)
            UUID universityId

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách phương thức xét tuyển thành công",

                        admissionMethodService
                                .getPublicMethods(
                                        universityId
                                )
                )
        );
    }
}