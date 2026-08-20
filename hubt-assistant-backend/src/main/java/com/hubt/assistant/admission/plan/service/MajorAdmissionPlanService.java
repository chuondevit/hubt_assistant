package com.hubt.assistant.admission.plan.service;

import com.hubt.assistant.admission.plan.dto.request.CreateMajorAdmissionPlanRequest;
import com.hubt.assistant.admission.plan.dto.request.UpdateApplicationOpenRequest;
import com.hubt.assistant.admission.plan.dto.request.UpdateMajorAdmissionPlanRequest;
import com.hubt.assistant.admission.plan.dto.response.MajorAdmissionPlanResponse;
import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;
import com.hubt.assistant.admission.plan.repository.MajorAdmissionPlanRepository;
import com.hubt.assistant.admission.plan.specification.MajorAdmissionPlanSpecification;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.repository.AdmissionYearRepository;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.repository.MajorRepository;

import com.hubt.assistant.organization.program.entity.Program;
import com.hubt.assistant.organization.program.repository.ProgramRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MajorAdmissionPlanService {

    private final MajorAdmissionPlanRepository
            majorAdmissionPlanRepository;

    private final AdmissionYearRepository
            admissionYearRepository;

    private final MajorRepository
            majorRepository;

    private final ProgramRepository
            programRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public MajorAdmissionPlanResponse create(
            CreateMajorAdmissionPlanRequest request
    ) {

        AdmissionYear admissionYear =
                getAdmissionYear(
                        request.admissionYearId()
                );


        Major major =
                getMajor(
                        request.majorId()
                );


        Program program = null;


        if (request.programId() != null) {

            program =
                    getProgram(
                            request.programId()
                    );


            validateProgramBelongsToMajor(
                    program,
                    major
            );
        }


        validateDuplicate(
                admissionYear.getId(),
                major.getId(),
                program != null
                        ? program.getId()
                        : null,
                null
        );


        MajorAdmissionPlan plan =
                new MajorAdmissionPlan();


        plan.setAdmissionYear(
                admissionYear
        );


        plan.setMajor(
                major
        );


        plan.setProgram(
                program
        );


        plan.setTotalQuota(
                request.totalQuota()
        );


        plan.setTuitionFee(
                request.tuitionFee()
        );


        plan.setExpectedCutoff(
                request.expectedCutoff()
        );


        plan.setApplicationOpen(
                request.applicationOpen() != null
                        ? request.applicationOpen()
                        : true
        );


        plan.setNotes(
                normalizeNullable(
                        request.notes()
                )
        );


        MajorAdmissionPlan saved =
                majorAdmissionPlanRepository
                        .save(
                                plan
                        );


        return toResponse(
                saved
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<MajorAdmissionPlanResponse>
    getAll(

            UUID admissionYearId,

            UUID majorId,

            UUID programId,

            Boolean applicationOpen,

            Pageable pageable

    ) {

        Specification<MajorAdmissionPlan> specification =
                MajorAdmissionPlanSpecification
                        .hasAdmissionYear(
                                admissionYearId
                        )
                        .and(
                                MajorAdmissionPlanSpecification
                                        .hasMajor(
                                                majorId
                                        )
                        )
                        .and(
                                MajorAdmissionPlanSpecification
                                        .hasProgram(
                                                programId
                                        )
                        )
                        .and(
                                MajorAdmissionPlanSpecification
                                        .hasApplicationOpen(
                                                applicationOpen
                                        )
                        );


        Page<MajorAdmissionPlanResponse> responsePage =
                majorAdmissionPlanRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(
                                this::toResponse
                        );


        return PageResponse.from(
                responsePage
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public MajorAdmissionPlanResponse getById(
            UUID id
    ) {

        return toResponse(
                getPlan(
                        id
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public MajorAdmissionPlanResponse update(

            UUID id,

            UpdateMajorAdmissionPlanRequest request

    ) {

        MajorAdmissionPlan plan =
                getPlan(
                        id
                );


        // =====================================================
        // ADMISSION YEAR
        // =====================================================

        if (request.admissionYearId() != null) {

            plan.setAdmissionYear(
                    getAdmissionYear(
                            request.admissionYearId()
                    )
            );
        }


        // =====================================================
        // MAJOR
        // =====================================================

        if (request.majorId() != null) {

            plan.setMajor(
                    getMajor(
                            request.majorId()
                    )
            );
        }


        // =====================================================
        // PROGRAM
        // =====================================================

        if (request.programId() != null) {

            Program program =
                    getProgram(
                            request.programId()
                    );


            validateProgramBelongsToMajor(
                    program,
                    plan.getMajor()
            );


            plan.setProgram(
                    program
            );
        }


        // Kiểm tra program hiện tại vẫn thuộc major
        // nếu major vừa được thay đổi
        if (plan.getProgram() != null) {

            validateProgramBelongsToMajor(
                    plan.getProgram(),
                    plan.getMajor()
            );
        }


        // =====================================================
        // DUPLICATE
        // =====================================================

        validateDuplicate(
                plan.getAdmissionYear()
                        .getId(),

                plan.getMajor()
                        .getId(),

                plan.getProgram() != null
                        ? plan.getProgram()
                        .getId()
                        : null,

                plan.getId()
        );


        // =====================================================
        // TOTAL QUOTA
        // =====================================================

        if (request.totalQuota() != null) {

            plan.setTotalQuota(
                    request.totalQuota()
            );
        }


        // =====================================================
        // TUITION FEE
        // =====================================================

        if (request.tuitionFee() != null) {

            plan.setTuitionFee(
                    request.tuitionFee()
            );
        }


        // =====================================================
        // EXPECTED CUTOFF
        // =====================================================

        if (request.expectedCutoff() != null) {

            plan.setExpectedCutoff(
                    request.expectedCutoff()
            );
        }


        // =====================================================
        // APPLICATION OPEN
        // =====================================================

        if (request.applicationOpen() != null) {

            plan.setApplicationOpen(
                    request.applicationOpen()
            );
        }


        // =====================================================
        // NOTES
        // =====================================================

        if (request.notes() != null) {

            plan.setNotes(
                    normalizeNullable(
                            request.notes()
                    )
            );
        }


        return toResponse(
                majorAdmissionPlanRepository
                        .save(
                                plan
                        )
        );
    }


    // =========================================================
    // UPDATE APPLICATION OPEN
    // =========================================================

    @Transactional
    public MajorAdmissionPlanResponse
    updateApplicationOpen(

            UUID id,

            UpdateApplicationOpenRequest request

    ) {

        MajorAdmissionPlan plan =
                getPlan(
                        id
                );


        plan.setApplicationOpen(
                request.applicationOpen()
        );


        return toResponse(
                majorAdmissionPlanRepository
                        .save(
                                plan
                        )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        MajorAdmissionPlan plan =
                getPlan(
                        id
                );


        majorAdmissionPlanRepository
                .delete(
                        plan
                );
    }


    // =========================================================
    // GET PLAN
    // =========================================================

    private MajorAdmissionPlan getPlan(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_PLAN_ID_REQUIRED",
                    "ID kế hoạch tuyển sinh ngành không được để trống"
            );
        }


        return majorAdmissionPlanRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "MAJOR_ADMISSION_PLAN_NOT_FOUND",
                                        "Không tìm thấy kế hoạch tuyển sinh ngành"
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
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "ADMISSION_YEAR_NOT_FOUND",
                                        "Không tìm thấy năm tuyển sinh"
                                )
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
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "MAJOR_NOT_FOUND",
                                        "Không tìm thấy ngành"
                                )
                );
    }


    // =========================================================
    // GET PROGRAM
    // =========================================================

    private Program getProgram(
            UUID id
    ) {

        return programRepository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "PROGRAM_NOT_FOUND",
                                        "Không tìm thấy chương trình đào tạo"
                                )
                );
    }


    // =========================================================
    // PROGRAM BELONGS TO MAJOR
    // =========================================================

    private void validateProgramBelongsToMajor(

            Program program,

            Major major

    ) {

        if (program.getMajor() == null
                || !program
                .getMajor()
                .getId()
                .equals(
                        major.getId()
                )) {

            throw new BusinessException(
                    "PROGRAM_MAJOR_MISMATCH",
                    "Chương trình đào tạo không thuộc ngành đã chọn"
            );
        }
    }


    // =========================================================
    // DUPLICATE
    // =========================================================

    private void validateDuplicate(

            UUID admissionYearId,

            UUID majorId,

            UUID programId,

            UUID currentId

    ) {

        boolean exists;


        // =====================================================
        // CREATE
        // =====================================================

        if (currentId == null) {

            if (programId == null) {

                exists =
                        majorAdmissionPlanRepository
                                .existsByAdmissionYearIdAndMajorIdAndProgramIsNull(
                                        admissionYearId,
                                        majorId
                                );

            } else {

                exists =
                        majorAdmissionPlanRepository
                                .existsByAdmissionYearIdAndMajorIdAndProgramId(
                                        admissionYearId,
                                        majorId,
                                        programId
                                );
            }

        }

        // =====================================================
        // UPDATE
        // =====================================================

        else {

            if (programId == null) {

                exists =
                        majorAdmissionPlanRepository
                                .existsByAdmissionYearIdAndMajorIdAndProgramIsNullAndIdNot(
                                        admissionYearId,
                                        majorId,
                                        currentId
                                );

            } else {

                exists =
                        majorAdmissionPlanRepository
                                .existsByAdmissionYearIdAndMajorIdAndProgramIdAndIdNot(
                                        admissionYearId,
                                        majorId,
                                        programId,
                                        currentId
                                );
            }
        }


        if (exists) {

            throw new BusinessException(
                    "MAJOR_ADMISSION_PLAN_EXISTS",
                    "Kế hoạch tuyển sinh của ngành/chương trình trong năm này đã tồn tại"
            );
        }
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private MajorAdmissionPlanResponse toResponse(
            MajorAdmissionPlan plan
    ) {

        Program program =
                plan.getProgram();


        return new MajorAdmissionPlanResponse(

                plan.getId(),

                plan.getAdmissionYear()
                        .getId(),

                plan.getAdmissionYear()
                        .getYear(),

                plan.getMajor()
                        .getId(),

                plan.getMajor()
                        .getCode(),

                plan.getMajor()
                        .getName(),

                program != null
                        ? program.getId()
                        : null,

                program != null
                        ? program.getCode()
                        : null,

                program != null
                        ? program.getName()
                        : null,

                plan.getTotalQuota(),

                plan.getTuitionFee(),

                plan.getExpectedCutoff(),

                plan.getApplicationOpen(),

                plan.getNotes(),

                plan.getCreatedAt(),

                plan.getUpdatedAt()
        );
    }


    // =========================================================
    // NORMALIZE
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