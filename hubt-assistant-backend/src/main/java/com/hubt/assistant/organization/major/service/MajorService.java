package com.hubt.assistant.organization.major.service;

import com.hubt.assistant.common.api.PageResponse;
import com.hubt.assistant.common.exception.BusinessException;

import com.hubt.assistant.organization.faculty.entity.Faculty;
import com.hubt.assistant.organization.faculty.entity.FacultyStatus;
import com.hubt.assistant.organization.faculty.repository.FacultyRepository;

import com.hubt.assistant.organization.major.dto.request.CreateMajorRequest;
import com.hubt.assistant.organization.major.dto.request.UpdateMajorRequest;

import com.hubt.assistant.organization.major.dto.response.MajorResponse;

import com.hubt.assistant.organization.major.entity.DegreeLevel;
import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.entity.MajorStatus;

import com.hubt.assistant.organization.major.repository.MajorRepository;
import com.hubt.assistant.organization.major.specification.MajorSpecification;

import com.hubt.assistant.organization.university.entity.University;
import com.hubt.assistant.organization.university.entity.UniversityStatus;
import com.hubt.assistant.organization.university.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MajorService {

    private static final int MAX_PAGE_SIZE = 100;


    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "createdAt",
                    "updatedAt",
                    "code",
                    "name",
                    "degreeLevel",
                    "durationYears",
                    "status"
            );


    private final MajorRepository
            majorRepository;

    private final UniversityRepository
            universityRepository;

    private final FacultyRepository
            facultyRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public MajorResponse create(
            CreateMajorRequest request
    ) {

        University university =
                getActiveUniversity(
                        request.universityId()
                );


        Faculty faculty =
                null;


        if (request.facultyId()
                != null) {

            faculty =
                    getActiveFaculty(
                            request.facultyId()
                    );


            validateFacultyUniversity(
                    faculty,
                    university
            );
        }


        String code =
                normalizeCode(
                        request.code()
                );


        if (majorRepository
                .existsByUniversity_IdAndCodeIgnoreCaseAndDeletedAtIsNull(
                        university.getId(),
                        code
                )) {

            throw new BusinessException(
                    "MAJOR_CODE_EXISTS",
                    "Mã ngành đã tồn tại trong trường này"
            );
        }


        Major major =
                new Major();


        major.setUniversity(
                university
        );


        major.setFaculty(
                faculty
        );


        major.setCode(
                code
        );


        major.setName(
                normalizeRequiredName(
                        request.name()
                )
        );


        major.setDegreeLevel(
                request.degreeLevel()
        );


        major.setDurationYears(
                request.durationYears()
        );


        major.setDescription(
                normalizeNullable(
                        request.description()
                )
        );


        major.setLearningOutcomes(
                normalizeNullable(
                        request.learningOutcomes()
                )
        );


        major.setCareerOpportunities(
                normalizeNullable(
                        request.careerOpportunities()
                )
        );


        major.setRequiredSkills(
                normalizeNullable(
                        request.requiredSkills()
                )
        );


        major.setThumbnailUrl(
                normalizeNullable(
                        request.thumbnailUrl()
                )
        );


        major.setStatus(
                MajorStatus.ACTIVE
        );


        return toResponse(
                majorRepository.save(
                        major
                )
        );
    }


    // =========================================================
    // ADMIN LIST
    // =========================================================

    @Transactional(readOnly = true)
    public PageResponse<MajorResponse>
    getAdminMajors(

            String keyword,

            UUID universityId,

            UUID facultyId,

            DegreeLevel degreeLevel,

            MajorStatus status,

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


        Specification<Major> specification =
                MajorSpecification
                        .notDeleted()
                        .and(
                                MajorSpecification
                                        .hasKeyword(
                                                keyword
                                        )
                        )
                        .and(
                                MajorSpecification
                                        .hasUniversityId(
                                                universityId
                                        )
                        )
                        .and(
                                MajorSpecification
                                        .hasFacultyId(
                                                facultyId
                                        )
                        )
                        .and(
                                MajorSpecification
                                        .hasDegreeLevel(
                                                degreeLevel
                                        )
                        )
                        .and(
                                MajorSpecification
                                        .hasStatus(
                                                status
                                        )
                        );


        Page<MajorResponse> result =
                majorRepository
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
    public MajorResponse getById(
            UUID id
    ) {

        return toResponse(
                getMajor(id)
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public MajorResponse update(

            UUID id,

            UpdateMajorRequest request

    ) {

        Major major =
                getMajor(
                        id
                );


        University university =
                major.getUniversity();


        if (request.universityId()
                != null) {

            university =
                    getActiveUniversity(
                            request.universityId()
                    );


            major.setUniversity(
                    university
            );
        }


        if (request.facultyId()
                != null) {

            Faculty faculty =
                    getActiveFaculty(
                            request.facultyId()
                    );


            validateFacultyUniversity(
                    faculty,
                    university
            );


            major.setFaculty(
                    faculty
            );
        }


        if (request.code()
                != null) {

            String code =
                    normalizeCode(
                            request.code()
                    );


            if (majorRepository
                    .existsByUniversity_IdAndCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(
                            university.getId(),
                            code,
                            id
                    )) {

                throw new BusinessException(
                        "MAJOR_CODE_EXISTS",
                        "Mã ngành đã tồn tại trong trường này"
                );
            }


            major.setCode(
                    code
            );
        }


        if (request.name()
                != null) {

            major.setName(
                    normalizeRequiredName(
                            request.name()
                    )
            );
        }


        if (request.degreeLevel()
                != null) {

            major.setDegreeLevel(
                    request.degreeLevel()
            );
        }


        if (request.durationYears()
                != null) {

            major.setDurationYears(
                    request.durationYears()
            );
        }


        if (request.description()
                != null) {

            major.setDescription(
                    normalizeNullable(
                            request.description()
                    )
            );
        }


        if (request.learningOutcomes()
                != null) {

            major.setLearningOutcomes(
                    normalizeNullable(
                            request.learningOutcomes()
                    )
            );
        }


        if (request.careerOpportunities()
                != null) {

            major.setCareerOpportunities(
                    normalizeNullable(
                            request.careerOpportunities()
                    )
            );
        }


        if (request.requiredSkills()
                != null) {

            major.setRequiredSkills(
                    normalizeNullable(
                            request.requiredSkills()
                    )
            );
        }


        if (request.thumbnailUrl()
                != null) {

            major.setThumbnailUrl(
                    normalizeNullable(
                            request.thumbnailUrl()
                    )
            );
        }


        /*
         * Nếu university bị đổi nhưng faculty hiện tại
         * không thuộc university mới thì reject.
         */
        if (major.getFaculty()
                != null) {

            validateFacultyUniversity(
                    major.getFaculty(),
                    university
            );
        }


        /*
         * Kiểm tra duplicate một lần nữa trong trường hợp
         * university đổi nhưng code không đổi.
         */
        if (majorRepository
                .existsByUniversity_IdAndCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(
                        university.getId(),
                        major.getCode(),
                        id
                )) {

            throw new BusinessException(
                    "MAJOR_CODE_EXISTS",
                    "Mã ngành đã tồn tại trong trường này"
            );
        }


        return toResponse(
                majorRepository.save(
                        major
                )
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional
    public MajorResponse updateStatus(

            UUID id,

            MajorStatus status

    ) {

        if (status == null) {

            throw new BusinessException(
                    "MAJOR_STATUS_REQUIRED",
                    "Trạng thái ngành không được để trống"
            );
        }


        Major major =
                getMajor(
                        id
                );


        major.setStatus(
                status
        );


        return toResponse(
                majorRepository.save(
                        major
                )
        );
    }


    // =========================================================
    // SOFT DELETE
    // =========================================================

    @Transactional
    public void delete(
            UUID id
    ) {

        Major major =
                getMajor(
                        id
                );


        major.setDeletedAt(
                Instant.now()
        );


        major.setStatus(
                MajorStatus.INACTIVE
        );


        majorRepository.save(
                major
        );
    }


    // =========================================================
    // PUBLIC LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<MajorResponse> getPublicMajors(

            UUID universityId,

            UUID facultyId

    ) {

        List<Major> majors;


        if (facultyId != null) {

            majors =
                    majorRepository
                            .findAllByFaculty_IdAndStatusAndDeletedAtIsNullOrderByNameAsc(
                                    facultyId,
                                    MajorStatus.ACTIVE
                            );

        } else if (universityId != null) {

            majors =
                    majorRepository
                            .findAllByUniversity_IdAndStatusAndDeletedAtIsNullOrderByNameAsc(
                                    universityId,
                                    MajorStatus.ACTIVE
                            );

        } else {

            majors =
                    majorRepository
                            .findAllByStatusAndDeletedAtIsNullOrderByNameAsc(
                                    MajorStatus.ACTIVE
                            );
        }


        return majors
                .stream()
                .filter(
                        this::isPublicMajor
                )
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // PUBLIC DETAIL BY CODE
    // =========================================================

    @Transactional(readOnly = true)
    public MajorResponse getPublicByCode(

            UUID universityId,

            String code

    ) {

        Major major =
                majorRepository
                        .findByUniversity_IdAndCodeIgnoreCaseAndDeletedAtIsNull(
                                universityId,
                                normalizeCode(
                                        code
                                )
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "MAJOR_NOT_FOUND",
                                                "Không tìm thấy ngành"
                                        )
                        );


        if (!isPublicMajor(
                major
        )) {

            throw new BusinessException(
                    "MAJOR_NOT_FOUND",
                    "Không tìm thấy ngành"
            );
        }


        return toResponse(
                major
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
                    "ID ngành không được để trống"
            );
        }


        return majorRepository
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
    }


    // =========================================================
    // UNIVERSITY
    // =========================================================

    private University getActiveUniversity(
            UUID id
    ) {

        University university =
                universityRepository
                        .findByIdAndDeletedAtIsNull(
                                id
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "UNIVERSITY_NOT_FOUND",
                                                "Không tìm thấy trường đại học"
                                        )
                        );


        if (university.getStatus()
                != UniversityStatus.ACTIVE) {

            throw new BusinessException(
                    "UNIVERSITY_NOT_ACTIVE",
                    "Trường đại học không hoạt động"
            );
        }


        return university;
    }


    // =========================================================
    // FACULTY
    // =========================================================

    private Faculty getActiveFaculty(
            UUID id
    ) {

        Faculty faculty =
                facultyRepository
                        .findById(
                                id
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                "FACULTY_NOT_FOUND",
                                                "Không tìm thấy khoa"
                                        )
                        );


        if (faculty.getStatus()
                != FacultyStatus.ACTIVE) {

            throw new BusinessException(
                    "FACULTY_NOT_ACTIVE",
                    "Khoa không hoạt động"
            );
        }


        return faculty;
    }


    private void validateFacultyUniversity(

            Faculty faculty,

            University university

    ) {

        if (!faculty
                .getUniversity()
                .getId()
                .equals(
                        university.getId()
                )) {

            throw new BusinessException(
                    "FACULTY_UNIVERSITY_MISMATCH",
                    "Khoa không thuộc trường đại học được chọn"
            );
        }
    }


    // =========================================================
    // PUBLIC CHECK
    // =========================================================

    private boolean isPublicMajor(
            Major major
    ) {

        if (major.getStatus()
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

    private MajorResponse toResponse(
            Major major
    ) {

        University university =
                major.getUniversity();


        Faculty faculty =
                major.getFaculty();


        return new MajorResponse(

                major.getId(),

                university.getId(),

                university.getCode(),

                university.getName(),

                faculty == null
                        ? null
                        : faculty.getId(),

                faculty == null
                        ? null
                        : faculty.getCode(),

                faculty == null
                        ? null
                        : faculty.getName(),

                major.getCode(),

                major.getName(),

                major.getDegreeLevel() == null
                        ? null
                        : major.getDegreeLevel()
                        .name(),

                major.getDurationYears(),

                major.getDescription(),

                major.getLearningOutcomes(),

                major.getCareerOpportunities(),

                major.getRequiredSkills(),

                major.getThumbnailUrl(),

                major.getStatus() == null
                        ? null
                        : major.getStatus()
                        .name(),

                major.getCreatedAt(),

                major.getUpdatedAt()
        );
    }


    // =========================================================
    // NORMALIZE
    // =========================================================

    private String normalizeCode(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "MAJOR_CODE_REQUIRED",
                    "Mã ngành không được để trống"
            );
        }


        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }


    private String normalizeRequiredName(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new BusinessException(
                    "MAJOR_NAME_REQUIRED",
                    "Tên ngành không được để trống"
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


    // =========================================================
    // PAGE
    // =========================================================

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


    // =========================================================
    // SORT
    // =========================================================

    private Sort createSort(

            String sortBy,

            String sortDirection

    ) {

        String field =
                sortBy == null
                        || sortBy.isBlank()
                        ? "createdAt"
                        : sortBy.trim();


        if (!ALLOWED_SORT_FIELDS
                .contains(
                        field
                )) {

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

            direction =
                    Sort.Direction.DESC;

        } else if ("asc".equalsIgnoreCase(
                sortDirection
        )) {

            direction =
                    Sort.Direction.ASC;

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