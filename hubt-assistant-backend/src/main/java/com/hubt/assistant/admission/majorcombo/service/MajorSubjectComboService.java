package com.hubt.assistant.admission.majorcombo.service;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;
import com.hubt.assistant.admission.combination.repository.SubjectCombinationRepository;

import com.hubt.assistant.admission.majorcombo.dto.request.CreateMajorSubjectComboRequest;
import com.hubt.assistant.admission.majorcombo.dto.request.UpdateMajorSubjectComboRequest;
import com.hubt.assistant.admission.majorcombo.dto.response.MajorSubjectComboResponse;

import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectCombo;
import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectComboStatus;

import com.hubt.assistant.admission.majorcombo.repository.MajorSubjectComboRepository;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;
import com.hubt.assistant.admission.planmethod.repository.MajorAdmissionMethodRepository;

import com.hubt.assistant.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MajorSubjectComboService {

    private final MajorSubjectComboRepository
            majorSubjectComboRepository;

    private final MajorAdmissionMethodRepository
            majorAdmissionMethodRepository;

    private final SubjectCombinationRepository
            subjectCombinationRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public MajorSubjectComboResponse create(
            CreateMajorSubjectComboRequest request
    ) {

        MajorAdmissionMethod majorMethod =
                getMajorAdmissionMethod(
                        request.majorAdmissionMethodId()
                );

        if (majorMethod.getStatus()
                != MajorAdmissionMethodStatus.ACTIVE) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_METHOD_NOT_ACTIVE",
                    "Phương thức xét tuyển của ngành hiện không hoạt động"
            );
        }

        SubjectCombination combo =
                getSubjectCombination(
                        request.subjectComboId()
                );

        if (majorSubjectComboRepository
                .existsByMajorAdmissionMethod_IdAndSubjectCombination_Id(
                        majorMethod.getId(),
                        combo.getId()
                )) {

            throw new BusinessException(
                    "MAJOR_SUBJECT_COMBO_EXISTS",
                    "Tổ hợp xét tuyển đã tồn tại trong phương thức này"
            );
        }

        MajorSubjectCombo entity =
                new MajorSubjectCombo();

        entity.setMajorAdmissionMethod(
                majorMethod
        );

        entity.setSubjectCombination(
                combo
        );

        entity.setMinimumScore(
                request.minimumScore()
        );

        entity.setStatus(
                MajorSubjectComboStatus.ACTIVE
        );

        return toResponse(
                majorSubjectComboRepository.save(
                        entity
                )
        );
    }


    // =========================================================
    // GET ALL BY MAJOR ADMISSION METHOD
    // =========================================================

    @Transactional(readOnly = true)
    public List<MajorSubjectComboResponse> getAll(
            UUID majorAdmissionMethodId
    ) {

        getMajorAdmissionMethod(
                majorAdmissionMethodId
        );

        return majorSubjectComboRepository
                .findAllByMajorAdmissionMethod_IdAndStatus(
                        majorAdmissionMethodId,
                        MajorSubjectComboStatus.ACTIVE
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public MajorSubjectComboResponse getById(
            UUID id
    ) {

        return toResponse(
                getEntity(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public MajorSubjectComboResponse update(

            UUID id,

            UpdateMajorSubjectComboRequest request
    ) {

        MajorSubjectCombo entity =
                getEntity(id);

        if (request.minimumScore() != null) {

            entity.setMinimumScore(
                    request.minimumScore()
            );
        }

        return toResponse(
                majorSubjectComboRepository.save(
                        entity
                )
        );
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Transactional
    public MajorSubjectComboResponse updateStatus(

            UUID id,

            MajorSubjectComboStatus status
    ) {

        if (status == null) {

            throw new BusinessException(
                    "STATUS_REQUIRED",
                    "Trạng thái không được để trống"
            );
        }

        MajorSubjectCombo entity =
                getEntity(id);

        entity.setStatus(
                status
        );

        return toResponse(
                majorSubjectComboRepository.save(
                        entity
                )
        );
    }


    // =========================================================
    // DELETE = SOFT DELETE
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        MajorSubjectCombo entity =
                getEntity(id);

        entity.setStatus(
                MajorSubjectComboStatus.INACTIVE
        );

        majorSubjectComboRepository.save(
                entity
        );
    }


    // =========================================================
    // INTERNAL
    // =========================================================

    private MajorSubjectCombo getEntity(
            UUID id
    ) {

        return majorSubjectComboRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "MAJOR_SUBJECT_COMBO_NOT_FOUND",
                                "Không tìm thấy tổ hợp xét tuyển của ngành"
                        )
                );
    }


    private MajorAdmissionMethod getMajorAdmissionMethod(
            UUID id
    ) {

        return majorAdmissionMethodRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "MAJOR_ADMISSION_METHOD_NOT_FOUND",
                                "Không tìm thấy phương thức xét tuyển của ngành"
                        )
                );
    }


    private SubjectCombination getSubjectCombination(
            UUID id
    ) {

        return subjectCombinationRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                "SUBJECT_COMBINATION_NOT_FOUND",
                                "Không tìm thấy tổ hợp xét tuyển"
                        )
                );
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private MajorSubjectComboResponse toResponse(
            MajorSubjectCombo entity
    ) {

        MajorAdmissionMethod majorMethod =
                entity.getMajorAdmissionMethod();

        var plan =
                majorMethod.getMajorAdmissionPlan();

        var major =
                plan.getMajor();

        var method =
                majorMethod.getAdmissionMethod();

        SubjectCombination combo =
                entity.getSubjectCombination();

        return new MajorSubjectComboResponse(

                entity.getId(),

                majorMethod.getId(),

                plan.getId(),

                major.getId(),
                major.getCode(),
                major.getName(),

                method.getId(),
                method.getCode(),
                method.getName(),

                combo.getId(),
                combo.getCode(),
                combo.getName(),

                entity.getMinimumScore(),

                entity.getStatus() == null
                        ? null
                        : entity.getStatus().name()
        );
    }
}