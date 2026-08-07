package com.hubt.assistant.identity.profile.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateProfileRequest;

import com.hubt.assistant.identity.profile.dto.response.AvatarUploadResponse;
import com.hubt.assistant.identity.profile.dto.response.CandidateProfileResponse;
import com.hubt.assistant.identity.profile.dto.response.ProfileCompletionResponse;

import com.hubt.assistant.identity.profile.service.CandidateProfileService;

import com.hubt.assistant.security.principal.CurrentUser;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/candidates/me")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;


    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>>
    getMyProfile(

            @AuthenticationPrincipal
            CurrentUser currentUser

    ) {

        CandidateProfileResponse result =
                candidateProfileService.getMyProfile(
                        currentUser.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy hồ sơ thí sinh thành công",
                        result
                )
        );
    }


    // =========================================================
    // UPDATE MY PROFILE
    // =========================================================

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>>
    updateMyProfile(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @Valid
            @RequestBody
            UpdateCandidateProfileRequest request

    ) {

        CandidateProfileResponse result =
                candidateProfileService.updateMyProfile(
                        currentUser.getId(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật hồ sơ thí sinh thành công",
                        result
                )
        );
    }


    // =========================================================
    // UPDATE AVATAR
    // =========================================================

    @PostMapping(
            value = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<AvatarUploadResponse>>
    updateAvatar(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @RequestPart("file")
            MultipartFile file

    ) {

        AvatarUploadResponse result =
                candidateProfileService.updateAvatar(
                        currentUser.getId(),
                        file
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật ảnh đại diện thành công",
                        result
                )
        );
    }


    // =========================================================
    // GET PROFILE COMPLETION
    // =========================================================

    @GetMapping("/profile-completion")
    public ResponseEntity<ApiResponse<ProfileCompletionResponse>>
    getProfileCompletion(

            @AuthenticationPrincipal
            CurrentUser currentUser

    ) {

        ProfileCompletionResponse result =
                candidateProfileService.getProfileCompletion(
                        currentUser.getId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy tiến độ hoàn thiện hồ sơ thành công",
                        result
                )
        );
    }
}