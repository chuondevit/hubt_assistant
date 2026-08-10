package com.hubt.assistant.identity.profile.controller;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.identity.profile.dto.request.AdminCandidateFilter;
import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateStatusRequest;

import com.hubt.assistant.identity.profile.dto.response.AdminCandidateDetailResponse;
import com.hubt.assistant.identity.profile.dto.response.AdminCandidateSummaryResponse;

import com.hubt.assistant.identity.profile.service.AdminCandidateService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/candidates")
@RequiredArgsConstructor
public class AdminCandidateController {

    private final AdminCandidateService
            adminCandidateService;


    // =========================================================
    // GET CANDIDATES
    // SEARCH + FILTER + PAGINATION + SORT
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<AdminCandidateSummaryResponse>
                    >
            > getCandidates(

            @RequestParam(
                    required = false
            )
            String keyword,


            @RequestParam(
                    required = false
            )
            String status,


            @RequestParam(
                    required = false
            )
            Boolean profileCompleted,


            @RequestParam(
                    defaultValue = "0"
            )
            int page,


            @RequestParam(
                    defaultValue = "20"
            )
            int size,


            @RequestParam(
                    defaultValue = "createdAt"
            )
            String sortBy,


            @RequestParam(
                    defaultValue = "desc"
            )
            String sortDirection

    ) {

        AdminCandidateFilter filter =
                new AdminCandidateFilter(
                        keyword,
                        status,
                        profileCompleted
                );


        PageResponse<AdminCandidateSummaryResponse> result =
                adminCandidateService
                        .getCandidates(
                                filter,
                                page,
                                size,
                                sortBy,
                                sortDirection
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách thí sinh thành công",
                        result
                )
        );
    }


    // =========================================================
    // GET CANDIDATE DETAIL
    // =========================================================

    @GetMapping("/{candidateId}")
    public ResponseEntity<
            ApiResponse<AdminCandidateDetailResponse>
            > getCandidateDetail(

            @PathVariable
            UUID candidateId

    ) {

        AdminCandidateDetailResponse result =
                adminCandidateService
                        .getCandidateDetail(
                                candidateId
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin thí sinh thành công",
                        result
                )
        );
    }


    // =========================================================
    // UPDATE CANDIDATE ACCOUNT STATUS
    // =========================================================

    @PatchMapping("/{candidateId}/status")
    public ResponseEntity<
            ApiResponse<AdminCandidateDetailResponse>
            > updateCandidateStatus(

            @PathVariable
            UUID candidateId,

            @Valid
            @RequestBody
            UpdateCandidateStatusRequest request

    ) {

        AdminCandidateDetailResponse result =
                adminCandidateService
                        .updateStatus(
                                candidateId,
                                request
                        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái thí sinh thành công",
                        result
                )
        );
    }
}