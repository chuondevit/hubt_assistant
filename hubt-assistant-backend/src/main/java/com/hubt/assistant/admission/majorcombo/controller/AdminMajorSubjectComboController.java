package com.hubt.assistant.admission.majorcombo.controller;

import com.hubt.assistant.admission.majorcombo.dto.request.CreateMajorSubjectComboRequest;
import com.hubt.assistant.admission.majorcombo.dto.request.UpdateMajorSubjectComboRequest;
import com.hubt.assistant.admission.majorcombo.dto.request.UpdateMajorSubjectComboStatusRequest;

import com.hubt.assistant.admission.majorcombo.dto.response.MajorSubjectComboResponse;

import com.hubt.assistant.admission.majorcombo.service.MajorSubjectComboService;

import com.hubt.assistant.common.api.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/major-subject-combos")
@RequiredArgsConstructor
public class AdminMajorSubjectComboController {

    private final MajorSubjectComboService
            majorSubjectComboService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<MajorSubjectComboResponse>
            > create(

            @Valid
            @RequestBody
            CreateMajorSubjectComboRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Thêm tổ hợp xét tuyển thành công",
                                majorSubjectComboService.create(
                                        request
                                )
                        )
                );
    }


    @GetMapping
    public ResponseEntity<
            ApiResponse<List<MajorSubjectComboResponse>>
            > getAll(

            @RequestParam
            UUID majorAdmissionMethodId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách tổ hợp xét tuyển thành công",
                        majorSubjectComboService.getAll(
                                majorAdmissionMethodId
                        )
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorSubjectComboResponse>
            > getById(

            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy tổ hợp xét tuyển thành công",
                        majorSubjectComboService.getById(
                                id
                        )
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<MajorSubjectComboResponse>
            > update(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateMajorSubjectComboRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật tổ hợp xét tuyển thành công",
                        majorSubjectComboService.update(
                                id,
                                request
                        )
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<
            ApiResponse<MajorSubjectComboResponse>
            > updateStatus(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateMajorSubjectComboStatusRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật trạng thái tổ hợp xét tuyển thành công",
                        majorSubjectComboService.updateStatus(
                                id,
                                request.status()
                        )
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable UUID id
    ) {

        majorSubjectComboService.delete(
                id
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vô hiệu hóa tổ hợp xét tuyển thành công",
                        null
                )
        );
    }
}