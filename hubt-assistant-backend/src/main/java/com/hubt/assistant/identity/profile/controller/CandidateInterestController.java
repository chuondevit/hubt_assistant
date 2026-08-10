package com.hubt.assistant.identity.profile.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.identity.profile.dto.request.CreateCandidateInterestRequest;
import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateInterestRequest;

import com.hubt.assistant.identity.profile.dto.response.CandidateInterestResponse;

import com.hubt.assistant.identity.profile.service.CandidateInterestService;

import com.hubt.assistant.security.principal.CurrentUser;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(
        "/api/v1/candidates/me/interests"
)
@RequiredArgsConstructor
public class CandidateInterestController {

    private final CandidateInterestService
            candidateInterestService;


    // =========================================================
    // GET MY INTERESTS
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<CandidateInterestResponse>>
            > getMyInterests(

            @AuthenticationPrincipal
            CurrentUser currentUser

    ) {

        List<CandidateInterestResponse> result =
                candidateInterestService
                        .getMyInterests(
                                currentUser.getId()
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách sở thích thành công",
                        result
                )
        );
    }


    // =========================================================
    // CREATE INTEREST
    // =========================================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<CandidateInterestResponse>
            > createInterest(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @Valid
            @RequestBody
            CreateCandidateInterestRequest request

    ) {

        CandidateInterestResponse result =
                candidateInterestService
                        .createInterest(
                                currentUser.getId(),
                                request
                        );


        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Thêm sở thích thành công",
                                result
                        )
                );
    }


    // =========================================================
    // UPDATE INTEREST
    // =========================================================

    @PutMapping(
            "/{interestCode}"
    )
    public ResponseEntity<
            ApiResponse<CandidateInterestResponse>
            > updateInterest(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @PathVariable
            String interestCode,

            @Valid
            @RequestBody
            UpdateCandidateInterestRequest request

    ) {

        CandidateInterestResponse result =
                candidateInterestService
                        .updateInterest(
                                currentUser.getId(),
                                interestCode,
                                request
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật sở thích thành công",
                        result
                )
        );
    }


    // =========================================================
    // DELETE INTEREST
    // =========================================================

    @DeleteMapping(
            "/{interestCode}"
    )
    public ResponseEntity<
            ApiResponse<Void>
            > deleteInterest(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @PathVariable
            String interestCode

    ) {

        candidateInterestService
                .deleteInterest(
                        currentUser.getId(),
                        interestCode
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa sở thích thành công"
                )
        );
    }
}