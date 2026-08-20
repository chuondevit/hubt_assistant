package com.hubt.assistant.organization.major.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.organization.major.dto.response.MajorResponse;

import com.hubt.assistant.organization.major.service.MajorService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/majors")
@RequiredArgsConstructor
public class PublicMajorController {

    private final MajorService
            majorService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MajorResponse>>
            > getMajors(

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            UUID facultyId

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách ngành thành công",
                        majorService.getPublicMajors(
                                universityId,
                                facultyId
                        )
                )
        );
    }


    @GetMapping("/{universityId}/{code}")
    public ResponseEntity<
            ApiResponse<MajorResponse>
            > getByCode(

            @PathVariable
            UUID universityId,

            @PathVariable
            String code

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin ngành thành công",
                        majorService.getPublicByCode(
                                universityId,
                                code
                        )
                )
        );
    }
}