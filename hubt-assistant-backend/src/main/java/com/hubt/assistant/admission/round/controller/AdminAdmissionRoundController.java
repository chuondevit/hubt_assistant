package com.hubt.assistant.admission.round.controller;

import com.hubt.assistant.admission.round.dto.request.CreateAdmissionRoundRequest;
import com.hubt.assistant.admission.round.dto.request.UpdateAdmissionRoundRequest;
import com.hubt.assistant.admission.round.dto.request.UpdateAdmissionRoundStatusRequest;

import com.hubt.assistant.admission.round.dto.response.AdmissionRoundResponse;

import com.hubt.assistant.admission.round.entity.AdmissionRoundStatus;

import com.hubt.assistant.admission.round.service.AdmissionRoundService;

import com.hubt.assistant.common.api.ApiResponse;
import com.hubt.assistant.common.api.PageResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admin/admission-rounds")
@RequiredArgsConstructor
public class AdminAdmissionRoundController {

    private final AdmissionRoundService
            admissionRoundService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<AdmissionRoundResponse>
            > create(

            @Valid
            @RequestBody
            CreateAdmissionRoundRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Tạo đợt tuyển sinh thành công",
                                admissionRoundService
                                        .create(request)
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<PageResponse<AdmissionRoundResponse>>
            > getRounds(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            UUID universityId,

            @RequestParam(required = false)
            UUID admissionYearId,

            @RequestParam(required = false)
            AdmissionRoundStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "roundNumber")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách đợt tuyển sinh thành công",

                        admissionRoundService
                                .getAdminRounds(
                                        keyword,
                                        universityId,
                                        admissionYearId,
                                        status,
                                        page,
                                        size,
                                        sortBy,
                                        sortDirection
                                )
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<AdmissionRoundResponse>
            > getById(

            @PathVariable
            UUID id

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy đợt tuyển sinh thành công",
                        admissionRoundService
                                .getById(id)
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<AdmissionRoundResponse>
            > update(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionRoundRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật đợt tuyển sinh thành công",
                        admissionRoundService
                                .update(
                                        id,
                                        request
                                )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<AdmissionRoundResponse>
            > updateStatus(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAdmissionRoundStatusRequest request

    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái đợt tuyển sinh thành công",
                        admissionRoundService
                                .updateStatus(
                                        id,
                                        request.status()
                                )
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Void>
            > delete(

            @PathVariable
            UUID id

    ) {

        admissionRoundService
                .delete(id);


        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa đợt tuyển sinh thành công",
                        null
                )
        );
    }
}