package com.hubt.assistant.organization.program.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.organization.program.dto.response.ProgramResponse;

import com.hubt.assistant.organization.program.service.ProgramService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/programs")
@RequiredArgsConstructor
public class PublicProgramController {

    private final ProgramService programService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<ProgramResponse>>
            >
    getPrograms(

            @RequestParam(required = false)
            UUID majorId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách chương trình đào tạo thành công",
                        programService
                                .getPublicPrograms(
                                        majorId
                                )
                )
        );
    }


    @GetMapping("/{majorId}/{code}")
    public ResponseEntity<
            ApiResponse<ProgramResponse>
            >
    getByCode(

            @PathVariable
            UUID majorId,

            @PathVariable
            String code
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin chương trình đào tạo thành công",
                        programService
                                .getPublicByCode(
                                        majorId,
                                        code
                                )
                )
        );
    }
}