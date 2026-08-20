package com.hubt.assistant.admission.year.controller;

import com.hubt.assistant.admission.year.dto.response.AdmissionYearResponse;
import com.hubt.assistant.admission.year.service.AdmissionYearService;

import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/admission-years")
@RequiredArgsConstructor
public class PublicAdmissionYearController {

    private final AdmissionYearService
            admissionYearService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<AdmissionYearResponse>>
            > getAdmissionYears(

            @RequestParam(required = false)
            UUID universityId

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách năm tuyển sinh thành công",
                        admissionYearService
                                .getPublicAdmissionYears(
                                        universityId
                                )
                )
        );
    }
}