package com.hubt.assistant.organization.program.service;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.entity.MajorStatus;
import com.hubt.assistant.organization.major.repository.MajorRepository;

import com.hubt.assistant.organization.program.dto.request.CreateProgramRequest;
import com.hubt.assistant.organization.program.dto.request.UpdateProgramRequest;

import com.hubt.assistant.organization.program.dto.response.ProgramResponse;

import com.hubt.assistant.organization.program.entity.Program;
import com.hubt.assistant.organization.program.entity.ProgramStatus;

import com.hubt.assistant.organization.program.repository.ProgramRepository;
import com.hubt.assistant.organization.program.specification.ProgramSpecification;

import com.hubt.assistant.organization.university.entity.UniversityStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "updatedAt",
                    "code",
                    "name",
                    "trainingMode",
                    "language",
                    "durationYears",
                    "status"
            );

    private final ProgramRepository programRepository;
    private final MajorRepository majorRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public ProgramResponse create(
            CreateProgramRequest request
    ) {

        Major major =
                getActiveMajor(
                        request.majorId()
                );

        String code =
                normalizeCode(
                        request.code()
                );

        if (programRepository
                .existsByMajor_IdAndCodeIgnoreCase(
                        major.getId(),
                        code
                )) {

            throw new BusinessException(
                    "PROGRAM_CODE_EXISTS",
                    "Mã chương trình đã tồn tại trong ngành này"
            );
        }

        Program program =
                new Program();

        program.setMajor(major);
        program.setCode(code);

        program.setName(
                normalizeRequiredName(
                        request.name()
                )
        );

        program.setTrainingMode(
                normalizeNullable(
                        request.trainingMode()
                )
        );

        program.setLanguage(
                normalizeNullable(
                        request.language()
                )
        );

        program.setDurationYears(
                request.durationYears()
        );

        program.setDescription(
                normalizeNullable(
                        request.description()
                )
        );

        program.setStatus(
                ProgramStatus.ACTIVE
        );

        return toResponse(
                programRepository.save(
                        program
                )
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<ProgramResponse>
    getAdminPrograms(

            String keyword,

            UUID universityId,

            UUID facultyId,

            UUID majorId,

            ProgramStatus status,

            int page,

            int size,

            String sortBy,

            String sortDirection
    ) {

        validatePagination(
                page,
                size
        );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        createSort(
                                sortBy,
                                sortDirection
                        )
                );

        Specification<Program> specification =
                ProgramSpecification
                        .hasKeyword(keyword)
                        .and(
                                ProgramSpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                ProgramSpecification
                                        .hasFacultyId(
                                                facultyId
                                        )
                        )
                        .and(
                                ProgramSpecification
                                        .hasMajorId(
                                                majorId
                                        )
                        )
                        .and(
                                ProgramSpecification
                                        .hasStatus(
                                                status
                                        )
                        );

        Page<ProgramResponse> result =
                programRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(
                                this::toResponse
                        );

        return PageResponse.from(
                result
        );
    }


    // =========================================================
    // DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public ProgramResponse getById(
            UUID id
    ) {

        return toResponse(
                getProgram(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public ProgramResponse update(
            UUID id,
            UpdateProgramRequest request
    ) {

        Program program =
                getProgram(id);

        Major major =
                program.getMajor();

        if (request.majorId() != null) {

            major =
                    getActiveMajor(
                            request.majorId()
                    );

            program.setMajor(
                    major
            );
        }

        if (request.code() != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );

            if (programRepository
                    .existsByMajor_IdAndCodeIgnoreCaseAndIdNot(
                            major.getId(),
                            code,
                            id
                    )) {

                throw new BusinessException(
                        "PROGRAM_CODE_EXISTS",
                        "Mã chương trình đã tồn tại trong ngành này"
                );
            }

            program.setCode(code);
        }

        /*
         * Major đổi nhưng code không đổi.
         */
        if (programRepository
                .existsByMajor_IdAndCodeIgnoreCaseAndIdNot(
                        major.getId(),
                        program.getCode(),
                        id
                )) {

            throw new BusinessException(
                    "PROGRAM_CODE_EXISTS",
                    "Mã chương trình đã tồn tại trong ngành này"
            );
        }

        if (request.name() != null) {

            program.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }

        if (request.trainingMode() != null) {

            program.setTrainingMode(
                    normalizeNullable(
                            request.trainingMode()
                    )
            );
        }

        if (request.language() != null) {

            program.setLanguage(
                    normalizeNullable(
                            request.language()
                    )
            );
        }

        if (request.durationYears() != null) {

            program.setDurationYears(
                    request.durationYears()
            );
        }

        if (request.description() != null) {

            program.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }

        return toResponse(
                programRepository.save(
                        program
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public ProgramResponse updateStatus(
            UUID id,
            ProgramStatus status
    ) {

        if (status == null) {

            throw new BusinessException(
                    "PROGRAM_STATUS_REQUIRED",
                    "Trạng thái chương trình không được để trống"
            );
        }

        Program program =
                getProgram(id);

        program.setStatus(
                status
        );

        return toResponse(
                programRepository.save(
                        program
                )
        );
    }


    // =========================================================
    // DELETE -> INACTIVE
    // DB programs không có deleted_at
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        Program program =
                getProgram(id);

        program.setStatus(
                ProgramStatus.INACTIVE
        );

        programRepository.save(
                program
        );
    }


    // =========================================================
    // PUBLIC LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<ProgramResponse>
    getPublicPrograms(
            UUID majorId
    ) {

        List<Program> programs;

        if (majorId != null) {

            programs =
                    programRepository
                            .findAllByMajor_IdAndStatusOrderByNameAsc(
                                    majorId,
                                    ProgramStatus.ACTIVE
                            );

        } else {

            programs =
                    programRepository
                            .findAllByStatusOrderByNameAsc(
                                    ProgramStatus.ACTIVE
                            );
        }

        return programs
                .stream()
                .filter(
                        this::isPublicProgram
                )
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // PUBLIC DETAIL
    // =========================================================

    @Transactional(readOnly = true)
    public ProgramResponse getPublicByCode(
            UUID majorId,
            String code
    ) {

        Program program =
                programRepository
                        .findByMajor_IdAndCodeIgnoreCase(
                                majorId,
                                normalizeCode(code)
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "PROGRAM_NOT_FOUND",
                                                "Không tìm thấy chương trình đào tạo"
                                        )
                        );

        if (!isPublicProgram(program)) {

            throw new BusinessException(
                    "PROGRAM_NOT_FOUND",
                    "Không tìm thấy chương trình đào tạo"
            );
        }

        return toResponse(program);
    }


    // =========================================================
    // GET PROGRAM
    // =========================================================

    private Program getProgram(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "PROGRAM_ID_REQUIRED",
                    "ID chương trình không được để trống"
            );
        }

        return programRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new BusinessException(
                                        "PROGRAM_NOT_FOUND",
                                        "Không tìm thấy chương trình đào tạo"
                                )
                );
    }


    // =========================================================
    // MAJOR
    // =========================================================

    private Major getActiveMajor(
            UUID id
    ) {

        if (id == null) {

            throw new BusinessException(
                    "MAJOR_ID_REQUIRED",
                    "Major ID không được để trống"
            );
        }

        Major major =
                majorRepository
                        .findByIdAndDeletedAtIsNull(
                                id
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "MAJOR_NOT_FOUND",
                                                "Không tìm thấy ngành"
                                        )
                        );

        if (major.getStatus()
                != MajorStatus.ACTIVE) {

            throw new BusinessException(
                    "MAJOR_NOT_ACTIVE",
                    "Ngành hiện không hoạt động"
            );
        }

        if (major.getUniversity()
                .getStatus()
                != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "UNIVERSITY_NOT_ACTIVE",
                    "Trường đại học hiện không hoạt động"
            );
        }

        if (major.getFaculty() != null
                && major.getFaculty().getStatus()
                != FacultyStatus.ACTIVE) {

            throw new BusinessException(
                    "FACULTY_NOT_ACTIVE",
                    "Khoa hiện không hoạt động"
            );
        }

        return major;
    }


    // =========================================================
    // PUBLIC CHECK
    // =========================================================

    private boolean isPublicProgram(
            Program program
    ) {

        if (program.getStatus()
                != ProgramStatus.ACTIVE) {

            return false;
        }

        Major major =
                program.getMajor();

        if (major.getDeletedAt() != null
                || major.getStatus()
                != MajorStatus.ACTIVE) {

            return false;
        }

        if (major.getUniversity()
                .getStatus()
                != UniversityStatus.ACTIVE) {

            return false;
        }

        return major.getFaculty() == null
                ||
                major.getFaculty()
                        .getStatus()
                        == FacultyStatus.ACTIVE;
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private ProgramResponse toResponse(
            Program program
    ) {

        Major major =
                program.getMajor();

        return new ProgramResponse(

                program.getId(),

                major.getId(),
                major.getCode(),
                major.getName(),

                major.getUniversity().getId(),
                major.getUniversity().getCode(),

                major.getFaculty() == null
                        ? null
                        : major.getFaculty().getId(),

                major.getFaculty() == null
                        ? null
                        : major.getFaculty().getCode(),

                program.getCode(),
                program.getName(),

                program.getTrainingMode(),
                program.getLanguage(),

                program.getDurationYears(),

                program.getDescription(),

                program.getStatus() == null
                        ? null
                        : program.getStatus().name(),

                program.getCreatedAt(),
                program.getUpdatedAt()
        );
    }


    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "PROGRAM_CODE_REQUIRED",
                    "Mã chương trình không được để trống"
            );
        }

        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }


    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "PROGRAM_NAME_REQUIRED",
                    "Tên chương trình không được để trống"
            );
        }

        return value.trim();
    }


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


    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new BusinessException(
                    "INVALID_PAGE",
                    "Page phải >= 0"
            );
        }

        if (size < 1
                || size > MAX_PAGE_SIZE) {

            throw new BusinessException(
                    "INVALID_PAGE_SIZE",
                    "Size phải từ 1 đến 100"
            );
        }
    }


    private Sort createSort(
            String sortBy,
            String sortDirection
    ) {

        String field =
                sortBy == null
                        || sortBy.isBlank()
                        ? "createdAt"
                        : sortBy.trim();

        if (!ALLOWED_SORT_FIELDS.contains(field)) {

            throw new BusinessException(
                    "INVALID_SORT_FIELD",
                    "Trường sắp xếp không hợp lệ"
            );
        }

        Sort.Direction direction;

        if (sortDirection == null
                || sortDirection.isBlank()
                || "desc".equalsIgnoreCase(
                        sortDirection
                )) {

            direction = Sort.Direction.DESC;

        } else if ("asc".equalsIgnoreCase(
                sortDirection
        )) {

            direction = Sort.Direction.ASC;

        } else {

            throw new BusinessException(
                    "INVALID_SORT_DIRECTION",
                    "sortDirection chỉ nhận asc hoặc desc"
            );
        }

        return Sort.by(
                direction,
                field
        );
    }
}