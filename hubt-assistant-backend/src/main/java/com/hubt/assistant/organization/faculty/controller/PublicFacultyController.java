package com.hubt.assistant.organization.faculty.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.organization.faculty.dto.response.FacultyResponse;

import com.hubt.assistant.organization.faculty.service.FacultyService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/faculties")
@RequiredArgsConstructor
public class PublicFacultyController {

    private final FacultyService
            facultyService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<FacultyResponse>>
            > getFaculties(

            @RequestParam(required = false)
            UUID universityId

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách khoa thành công",
                        facultyService
                                .getPublicFaculties(
                                        universityId
                                )
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<FacultyResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin khoa thành công",
                        facultyService
                                .getPublicById(
                                        id
                                )
                )
        );
    }
}