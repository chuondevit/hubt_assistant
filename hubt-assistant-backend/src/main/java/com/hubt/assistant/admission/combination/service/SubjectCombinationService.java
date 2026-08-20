package com.hubt.assistant.admission.combination.service;

import com.hubt.assistant.admission.combination.dto.request.CreateSubjectCombinationRequest;
import com.hubt.assistant.admission.combination.dto.request.SubjectCombinationItemRequest;
import com.hubt.assistant.admission.combination.dto.request.UpdateSubjectCombinationRequest;

import com.hubt.assistant.admission.combination.dto.response.SubjectCombinationItemResponse;
import com.hubt.assistant.admission.combination.dto.response.SubjectCombinationResponse;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;
import com.hubt.assistant.admission.combination.entity.SubjectCombinationItem;

import com.hubt.assistant.admission.combination.repository.SubjectCombinationRepository;

import com.hubt.assistant.admission.subject.entity.Subject;
import com.hubt.assistant.admission.subject.repository.SubjectRepository;

import com.hubt.assistant.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SubjectCombinationService {

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final SubjectCombinationRepository
            subjectCombinationRepository;

    private final SubjectRepository
            subjectRepository;


    // =========================================================
    // CREATE SUBJECT COMBINATION
    // =========================================================

    @Transactional
    public SubjectCombinationResponse create(
            CreateSubjectCombinationRequest request
    ) {

        // -----------------------------------------------------
        // Normalize code
        // -----------------------------------------------------

        String code =
                normalizeCode(
                        request.code()
                );


        // -----------------------------------------------------
        // Check duplicate code
        // -----------------------------------------------------

        if (subjectCombinationRepository
                .existsByCodeIgnoreCase(
                        code
                )) {

            throw new BusinessException(
                    "SUBJECT_COMBINATION_CODE_EXISTS",
                    "Mã tổ hợp môn đã tồn tại"
            );
        }


        // -----------------------------------------------------
        // Validate items
        // -----------------------------------------------------

        validateItems(
                request.items()
        );


        // -----------------------------------------------------
        // Create combination
        // -----------------------------------------------------

        SubjectCombination combination =
                new SubjectCombination();


        combination.setCode(
                code
        );


        combination.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        combination.setDescription(
                normalizeNullable(
                        request.description()
                )
        );


        // -----------------------------------------------------
        // Add subjects
        // -----------------------------------------------------

        addItems(
                combination,
                request.items()
        );


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        SubjectCombination saved =
                subjectCombinationRepository
                        .save(
                                combination
                        );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // GET ALL SUBJECT COMBINATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<SubjectCombinationResponse> getAll() {

        return subjectCombinationRepository
                .findAllByOrderByCodeAsc()
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // GET SUBJECT COMBINATION BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public SubjectCombinationResponse getById(
            UUID id
    ) {

        SubjectCombination combination =
                getCombination(
                        id
                );


        return toResponse(
                combination
        );
    }


    // =========================================================
    // UPDATE SUBJECT COMBINATION
    // =========================================================

    @Transactional
    public SubjectCombinationResponse update(

            UUID id,

            UpdateSubjectCombinationRequest request

    ) {

        // -----------------------------------------------------
        // Find combination
        // -----------------------------------------------------

        SubjectCombination combination =
                getCombination(
                        id
                );


        // =====================================================
        // CODE
        // =====================================================

        if (request.code() != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            if (subjectCombinationRepository
                    .existsByCodeIgnoreCaseAndIdNot(
                            code,
                            id
                    )) {

                throw new BusinessException(
                        "SUBJECT_COMBINATION_CODE_EXISTS",
                        "Mã tổ hợp môn đã tồn tại"
                );
            }


            combination.setCode(
                    code
            );
        }


        // =====================================================
        // NAME
        // =====================================================

        if (request.name() != null) {

            combination.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        // =====================================================
        // DESCRIPTION
        // =====================================================

        if (request.description() != null) {

            combination.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }


        // =====================================================
        // ITEMS
        // =====================================================

        if (request.items() != null) {

            validateItems(
                    request.items()
            );


            /*
             * orphanRemoval = true
             *
             * Khi clear list, Hibernate sẽ xóa
             * các subject_combo_items cũ.
             */
            combination
                    .getItems()
                    .clear();


            /*
             * Flush không bắt buộc trong đa số trường hợp,
             * nhưng việc save cuối transaction sẽ xử lý.
             */
            addItems(
                    combination,
                    request.items()
            );
        }


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        SubjectCombination saved =
                subjectCombinationRepository
                        .save(
                                combination
                        );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // DELETE SUBJECT COMBINATION
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        SubjectCombination combination =
                getCombination(
                        id
                );


        /*
         * subject_combo_items có FK:
         *
         * subject_combo_id
         * REFERENCES subject_combos(id)
         * ON DELETE CASCADE
         *
         * Ngoài ra entity cũng đang:
         *
         * cascade = CascadeType.ALL
         * orphanRemoval = true
         */
        subjectCombinationRepository
                .delete(
                        combination
                );
    }


    // =========================================================
    // ADD ITEMS
    // =========================================================

    private void addItems(

            SubjectCombination combination,

            List<SubjectCombinationItemRequest> requests

    ) {

        for (SubjectCombinationItemRequest request
                : requests) {


            // -------------------------------------------------
            // Find subject
            // -------------------------------------------------

            Subject subject =
                    subjectRepository
                            .findById(
                                    request.subjectId()
                            )
                            .orElseThrow(
                                    () ->
                                            new BusinessException(
                                                    "SUBJECT_NOT_FOUND",
                                                    "Không tìm thấy môn học: "
                                                            + request.subjectId()
                                            )
                            );


            // -------------------------------------------------
            // Create item
            // -------------------------------------------------

            SubjectCombinationItem item =
                    new SubjectCombinationItem();


            /*
             * IMPORTANT:
             *
             * SubjectCombinationItem dùng:
             *
             * @EmbeddedId
             * @MapsId("subjectCombinationId")
             * @MapsId("subjectId")
             *
             * Vì vậy KHÔNG tạo UUID id riêng.
             *
             * Hibernate sẽ lấy PK từ:
             *
             * combination.id + subject.id
             */


            item.setSubjectCombination(
                    combination
            );


            item.setSubject(
                    subject
            );


            item.setCoefficient(
                    request.coefficient()
            );


            // -------------------------------------------------
            // Add child to parent
            // -------------------------------------------------

            combination
                    .getItems()
                    .add(
                            item
                    );
        }
    }


    // =========================================================
    // VALIDATE ITEMS
    // =========================================================

    private void validateItems(
            List<SubjectCombinationItemRequest> items
    ) {

        // -----------------------------------------------------
        // Empty list
        // -----------------------------------------------------

        if (items == null
                || items.isEmpty()) {

            throw new BusinessException(
                    "SUBJECT_COMBINATION_ITEMS_REQUIRED",
                    "Tổ hợp phải có ít nhất một môn học"
            );
        }


        // -----------------------------------------------------
        // Check duplicated subjects
        // -----------------------------------------------------

        Set<UUID> subjectIds =
                new HashSet<>();


        for (SubjectCombinationItemRequest item
                : items) {


            if (item.subjectId() == null) {

                throw new BusinessException(
                        "SUBJECT_ID_REQUIRED",
                        "Subject ID không được để trống"
                );
            }


            if (item.coefficient() == null) {

                throw new BusinessException(
                        "SUBJECT_COEFFICIENT_REQUIRED",
                        "Hệ số môn học không được để trống"
                );
            }


            if (item.coefficient().signum()
                    <= 0) {

                throw new BusinessException(
                        "SUBJECT_COEFFICIENT_INVALID",
                        "Hệ số môn học phải lớn hơn 0"
                );
            }


            if (!subjectIds.add(
                    item.subjectId()
            )) {

                throw new BusinessException(
                        "DUPLICATE_SUBJECT",
                        "Một môn học không được xuất hiện nhiều lần trong cùng tổ hợp"
                );
            }
        }
    }


    // =========================================================
    // GET COMBINATION
    // =========================================================

    private SubjectCombination getCombination(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "SUBJECT_COMBINATION_ID_REQUIRED",
                    "ID tổ hợp môn không được để trống"
            );
        }


        return subjectCombinationRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "SUBJECT_COMBINATION_NOT_FOUND",
                                        "Không tìm thấy tổ hợp môn"
                                )
                );
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private SubjectCombinationResponse toResponse(
            SubjectCombination combination
    ) {

        List<SubjectCombinationItemResponse> items =
                combination
                        .getItems()
                        .stream()
                        .map(
                                item ->
                                        new SubjectCombinationItemResponse(

                                                item
                                                        .getSubject()
                                                        .getId(),

                                                item
                                                        .getSubject()
                                                        .getCode(),

                                                item
                                                        .getSubject()
                                                        .getName(),

                                                item
                                                        .getCoefficient()
                                        )
                        )
                        .toList();


        return new SubjectCombinationResponse(

                combination.getId(),

                combination.getCode(),

                combination.getName(),

                combination.getDescription(),

                items
        );
    }


    // =========================================================
    // NORMALIZE CODE
    // =========================================================

    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "SUBJECT_COMBINATION_CODE_REQUIRED",
                    "Mã tổ hợp môn không được để trống"
            );
        }


        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }


    // =========================================================
    // NORMALIZE REQUIRED NAME
    // =========================================================

    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "SUBJECT_COMBINATION_NAME_REQUIRED",
                    "Tên tổ hợp môn không được để trống"
            );
        }


        return value.trim();
    }


    // =========================================================
    // NORMALIZE NULLABLE
    // =========================================================

    private String normalizeNullable(
            String value
    ) {

        if (value == null) {

            return null;
        }


        String result =
                value.trim();


        return result.isBlank()
                ? null
                : result;
    }
}