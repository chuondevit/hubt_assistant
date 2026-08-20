package com.hubt.assistant.organization.university.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.organization.university.dto.response.UniversityResponse;

import com.hubt.assistant.organization.university.service.UniversityService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/public/universities")
@RequiredArgsConstructor
public class PublicUniversityController {

    private final UniversityService universityService;


    // =========================================================
    // GET PUBLIC UNIVERSITY LIST
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<UniversityResponse>>
            > getUniversities() {

        List<UniversityResponse> result =
                universityService
                        .getPublicUniversities();


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách trường đại học thành công",
                        result
                )
        );
    }


    // =========================================================
    // GET PUBLIC UNIVERSITY DETAIL BY CODE
    // =========================================================

    @GetMapping("/{code}")
    public ResponseEntity<
            ApiResponse<UniversityResponse>
            > getUniversityByCode(

            @PathVariable
            String code

    ) {

        UniversityResponse result =
                universityService
                        .getPublicByCode(
                                code
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin trường đại học thành công",
                        result
                )
        );
    }
}