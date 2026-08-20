package com.hubt.assistant.admission.round.controller;

import com.hubt.assistant.admission.round.dto.response.AdmissionRoundResponse;

import com.hubt.assistant.admission.round.service.AdmissionRoundService;

import com.hubt.assistant.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/public/admission-rounds")
@RequiredArgsConstructor
public class PublicAdmissionRoundController {

    private final AdmissionRoundService
            admissionRoundService;


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<AdmissionRoundResponse>>
            > getRounds(

            @RequestParam
            UUID admissionYearId

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách đợt tuyển sinh thành công",

                        admissionRoundService
                                .getPublicRounds(
                                        admissionYearId
                                )
                )
        );
    }
}