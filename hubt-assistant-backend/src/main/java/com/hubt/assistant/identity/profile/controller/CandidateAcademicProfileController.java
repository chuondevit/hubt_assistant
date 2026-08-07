package com.hubt.assistant.identity.profile.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateAcademicProfileRequest;
import com.hubt.assistant.identity.profile.dto.response.CandidateAcademicProfileResponse;

import com.hubt.assistant.identity.profile.service.CandidateAcademicProfileService;

import com.hubt.assistant.security.principal.CurrentUser;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidates/me/academic-profile")
@RequiredArgsConstructor
public class CandidateAcademicProfileController {

    private final CandidateAcademicProfileService
            academicProfileService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<CandidateAcademicProfileResponse>
            > getMyAcademicProfile(

            @AuthenticationPrincipal
            CurrentUser currentUser

    ) {

        CandidateAcademicProfileResponse result =
                academicProfileService
                        .getMyAcademicProfile(
                                currentUser.getId()
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy hồ sơ học tập thành công",
                        result
                )
        );
    }


    @PutMapping
    public ResponseEntity<
            ApiResponse<CandidateAcademicProfileResponse>
            > updateMyAcademicProfile(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @Valid
            @RequestBody
            UpdateCandidateAcademicProfileRequest request

    ) {

        CandidateAcademicProfileResponse result =
                academicProfileService
                        .updateMyAcademicProfile(
                                currentUser.getId(),
                                request
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật hồ sơ học tập thành công",
                        result
                )
        );
    }
}