package com.hubt.assistant.admission.publicapi.controller;

import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionOverviewResponse;
import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionSearchResponse;

import com.hubt.assistant.admission.publicapi.service.PublicAdmissionSearchService;
import com.hubt.assistant.admission.publicapi.service.PublicAdmissionService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/admissions")
@RequiredArgsConstructor
public class PublicAdmissionController {

    private final PublicAdmissionService
            publicAdmissionService;

    private final PublicAdmissionSearchService
            publicAdmissionSearchService;


    // =========================================================
    // 4.10
    // GET FULL ADMISSION INFORMATION OF MAJOR
    // =========================================================

    @GetMapping("/majors/{majorId}")
    public ResponseEntity<
            ApiResponse<PublicAdmissionOverviewResponse>
            > getMajorAdmission(

            @PathVariable
            UUID majorId,

            @RequestParam
            UUID admissionYearId

    ) {

        PublicAdmissionOverviewResponse result =
                publicAdmissionService
                        .getMajorAdmissionOverview(
                                majorId,
                                admissionYearId
                        );


        return ResponseEntity.ok(

                ApiResponse.success(
                        "Lấy thông tin tuyển sinh ngành thành công",
                        result
                )
        );
    }


    // =========================================================
    // 4.11
    // SEARCH + FILTER + PAGINATION
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<
            ApiResponse<
                    PageResponse<PublicAdmissionSearchResponse>
                    >
            > search(

            // -------------------------------------------------
            // SEARCH
            // -------------------------------------------------

            @RequestParam(required = false)
            String keyword,


            // -------------------------------------------------
            // FILTER UNIVERSITY
            // -------------------------------------------------

            @RequestParam(required = false)
            UUID universityId,


            // -------------------------------------------------
            // FILTER ADMISSION YEAR
            // -------------------------------------------------

            @RequestParam(required = false)
            UUID admissionYearId,


            // -------------------------------------------------
            // FILTER ADMISSION METHOD
            // -------------------------------------------------

            @RequestParam(required = false)
            UUID admissionMethodId,


            // -------------------------------------------------
            // FILTER SUBJECT COMBINATION
            // -------------------------------------------------

            @RequestParam(required = false)
            UUID subjectComboId,


            // -------------------------------------------------
            // FILTER CUTOFF
            // -------------------------------------------------

            @RequestParam(required = false)
            BigDecimal minCutoff,


            @RequestParam(required = false)
            BigDecimal maxCutoff,


            // -------------------------------------------------
            // APPLICATION STATUS
            // Public mặc định chỉ lấy plan đang mở
            // -------------------------------------------------

            @RequestParam(
                    defaultValue = "true"
            )
            Boolean applicationOpen,


            // -------------------------------------------------
            // PAGINATION
            // -------------------------------------------------

            @RequestParam(
                    defaultValue = "0"
            )
            int page,


            @RequestParam(
                    defaultValue = "20"
            )
            int size,


            // -------------------------------------------------
            // SORT
            // -------------------------------------------------

            @RequestParam(
                    defaultValue = "createdAt"
            )
            String sortBy,


            @RequestParam(
                    defaultValue = "desc"
            )
            String sortDirection

    ) {

        PageResponse<PublicAdmissionSearchResponse> result =
                publicAdmissionSearchService
                        .search(

                                keyword,

                                universityId,

                                admissionYearId,

                                admissionMethodId,

                                subjectComboId,

                                minCutoff,

                                maxCutoff,

                                applicationOpen,

                                page,

                                size,

                                sortBy,

                                sortDirection
                        );


        return ResponseEntity.ok(

                ApiResponse.success(
                        "Tìm kiếm thông tin tuyển sinh thành công",
                        result
                )
        );
    }
}