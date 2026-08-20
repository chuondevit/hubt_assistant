package com.hubt.assistant.admission.publicapi.service;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoff;
import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;
import com.hubt.assistant.admission.cutoff.repository.MajorCutoffRepository;

import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectCombo;
import com.hubt.assistant.admission.majorcombo.entity.MajorSubjectComboStatus;
import com.hubt.assistant.admission.majorcombo.repository.MajorSubjectComboRepository;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;
import com.hubt.assistant.admission.plan.repository.MajorAdmissionPlanRepository;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;
import com.hubt.assistant.admission.planmethod.repository.MajorAdmissionMethodRepository;

import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionComboResponse;
import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionMethodResponse;
import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionOverviewResponse;
import com.hubt.assistant.admission.publicapi.dto.PublicAdmissionPlanResponse;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;
import com.hubt.assistant.admission.year.repository.AdmissionYearRepository;

import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.entity.MajorStatus;
import com.hubt.assistant.organization.major.repository.MajorRepository;

import com.hubt.assistant.organization.program.entity.Program;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicAdmissionService {

    private final MajorRepository
            majorRepository;

    private final AdmissionYearRepository
            admissionYearRepository;

    private final MajorAdmissionPlanRepository
            majorAdmissionPlanRepository;

    private final MajorAdmissionMethodRepository
            majorAdmissionMethodRepository;

    private final MajorSubjectComboRepository
            majorSubjectComboRepository;

    private final MajorCutoffRepository
            majorCutoffRepository;


    // =========================================================
    // GET MAJOR ADMISSION OVERVIEW
    // =========================================================

    @Transactional(readOnly = true)
    public PublicAdmissionOverviewResponse
    getMajorAdmissionOverview(

            UUID majorId,

            UUID admissionYearId
    ) {

        // =====================================================
        // MAJOR
        // =====================================================

        Major major =
                getMajor(
                        majorId
                );

        if (major.getStatus()
                != MajorStatus.ACTIVE) {

            throw new BusinessException(
                    "MAJOR_NOT_ACTIVE",
                    "Ngành hiện không hoạt động"
            );
        }


        // =====================================================
        // ADMISSION YEAR
        // =====================================================

        AdmissionYear admissionYear =
                getAdmissionYear(
                        admissionYearId
                );


        // Kiểm tra năm tuyển sinh thuộc cùng trường với ngành
        UUID majorUniversityId =
                major.getUniversity()
                        .getId();

        UUID yearUniversityId =
                admissionYear
                        .getUniversity()
                        .getId();


        if (!majorUniversityId.equals(
                yearUniversityId
        )) {

            throw new BusinessException(
                    "ADMISSION_YEAR_UNIVERSITY_MISMATCH",
                    "Năm tuyển sinh không thuộc trường của ngành"
            );
        }


        /*
         * Public chỉ cho phép xem năm đang OPEN.
         *
         * Nếu bạn muốn public cả CLOSED để xem
         * điểm chuẩn năm cũ thì có thể mở rộng sau.
         */
        if (admissionYear.getStatus()
                != AdmissionYearStatus.OPEN) {

            throw new BusinessException(
                    "ADMISSION_YEAR_NOT_OPEN",
                    "Năm tuyển sinh hiện không mở công khai"
            );
        }


        // =====================================================
        // PLANS
        // =====================================================

        List<MajorAdmissionPlan> plans =
                majorAdmissionPlanRepository
                        .findAllByMajor_IdAndAdmissionYear_IdAndApplicationOpenTrueOrderByCreatedAtDesc(

                                majorId,

                                admissionYearId
                        );


        List<PublicAdmissionPlanResponse> planResponses =
                plans
                        .stream()
                        .map(
                                plan ->
                                        mapPlan(
                                                plan,
                                                admissionYear,
                                                major
                                        )
                        )
                        .toList();


        return new PublicAdmissionOverviewResponse(

                major.getUniversity()
                        .getId(),

                major.getUniversity()
                        .getCode(),

                major.getUniversity()
                        .getName(),

                major.getId(),

                major.getCode(),

                major.getName(),

                admissionYear.getId(),

                admissionYear.getYear(),

                admissionYear.getName(),

                planResponses
        );
    }


    // =========================================================
    // MAP PLAN
    // =========================================================

    private PublicAdmissionPlanResponse mapPlan(

            MajorAdmissionPlan plan,

            AdmissionYear admissionYear,

            Major major
    ) {

        List<MajorAdmissionMethod> methods =
                majorAdmissionMethodRepository
                        .findAllByMajorAdmissionPlan_IdAndStatusOrderByAdmissionMethod_NameAsc(

                                plan.getId(),

                                MajorAdmissionMethodStatus.ACTIVE
                        );


        List<PublicAdmissionMethodResponse> methodResponses =
                methods
                        .stream()
                        .map(
                                method ->
                                        mapMethod(
                                                method,
                                                admissionYear,
                                                major
                                        )
                        )
                        .toList();


        Program program =
                plan.getProgram();


        return new PublicAdmissionPlanResponse(

                plan.getId(),

                program == null
                        ? null
                        : program.getId(),

                program == null
                        ? null
                        : program.getCode(),

                program == null
                        ? null
                        : program.getName(),

                plan.getTotalQuota(),

                plan.getTuitionFee(),

                plan.getExpectedCutoff(),

                plan.getApplicationOpen(),

                plan.getNotes(),

                methodResponses
        );
    }


    // =========================================================
    // MAP METHOD
    // =========================================================

    private PublicAdmissionMethodResponse mapMethod(

            MajorAdmissionMethod majorMethod,

            AdmissionYear admissionYear,

            Major major
    ) {

        List<MajorSubjectCombo> combinations =
                majorSubjectComboRepository
                        .findAllByMajorAdmissionMethod_IdAndStatus(

                                majorMethod.getId(),

                                MajorSubjectComboStatus.ACTIVE
                        );


        List<PublicAdmissionComboResponse> comboResponses =
                combinations
                        .stream()
                        .map(
                                combo ->
                                        mapCombo(
                                                combo,
                                                majorMethod,
                                                admissionYear,
                                                major
                                        )
                        )
                        .toList();


        Map<String, Object> conditions =
                majorMethod.getConditionsJson() == null

                        ? Map.of()

                        : majorMethod.getConditionsJson();


        return new PublicAdmissionMethodResponse(

                majorMethod.getId(),

                majorMethod
                        .getAdmissionMethod()
                        .getId(),

                majorMethod
                        .getAdmissionMethod()
                        .getCode(),

                majorMethod
                        .getAdmissionMethod()
                        .getName(),

                majorMethod
                        .getAdmissionMethod()
                        .getDescription(),

                majorMethod.getQuota(),

                majorMethod.getMinimumScore(),

                conditions,

                comboResponses
        );
    }


    // =========================================================
    // MAP COMBINATION + CUTOFF
    // =========================================================

    private PublicAdmissionComboResponse mapCombo(

            MajorSubjectCombo majorCombo,

            MajorAdmissionMethod majorMethod,

            AdmissionYear admissionYear,

            Major major
    ) {

        var combo =
                majorCombo
                        .getSubjectCombination();


        MajorCutoff cutoff =
                majorCutoffRepository
                        .findTopByAdmissionYear_IdAndMajor_IdAndAdmissionMethod_IdAndSubjectCombination_IdAndStatusAndPublishedAtIsNotNullOrderByPublishedAtDesc(

                                admissionYear.getId(),

                                major.getId(),

                                majorMethod
                                        .getAdmissionMethod()
                                        .getId(),

                                combo.getId(),

                                MajorCutoffStatus.ACTIVE
                        )
                        .filter(
                                item ->
                                        item.getPublishedAt() != null

                                                && !item
                                                .getPublishedAt()
                                                .isAfter(
                                                        Instant.now()
                                                )
                        )
                        .orElse(null);


        return new PublicAdmissionComboResponse(

                combo.getId(),

                combo.getCode(),

                combo.getName(),

                majorCombo.getMinimumScore(),

                cutoff == null
                        ? null
                        : cutoff.getCutoffScore(),

                cutoff == null
                        ? null
                        : cutoff.getPublishedAt()
        );
    }


    // =========================================================
    // GET MAJOR
    // =========================================================

    private Major getMajor(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "MAJOR_ID_REQUIRED",
                    "Major ID không được để trống"
            );
        }


        return majorRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "MAJOR_NOT_FOUND",
                                        "Không tìm thấy ngành"
                                )
                );
    }


    // =========================================================
    // GET ADMISSION YEAR
    // =========================================================

    private AdmissionYear getAdmissionYear(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "ADMISSION_YEAR_ID_REQUIRED",
                    "Admission Year ID không được để trống"
            );
        }


        return admissionYearRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "ADMISSION_YEAR_NOT_FOUND",
                                        "Không tìm thấy năm tuyển sinh"
                                )
                );
    }
}