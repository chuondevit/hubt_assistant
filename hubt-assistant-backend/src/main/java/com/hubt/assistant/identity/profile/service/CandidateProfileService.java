package com.hubt.assistant.identity.profile.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.common.exception.ResourceNotFoundException;

import com.hubt.assistant.identity.profile.dto.request.UpdateCandidateProfileRequest;

import com.hubt.assistant.identity.profile.dto.response.AvatarUploadResponse;
import com.hubt.assistant.identity.profile.dto.response.CandidateProfileResponse;
import com.hubt.assistant.identity.profile.dto.response.ProfileCompletionResponse;

import com.hubt.assistant.identity.profile.entity.CandidateProfile;
import com.hubt.assistant.identity.profile.repository.CandidateProfileRepository;

import com.hubt.assistant.identity.user.entity.AccountStatus;
import com.hubt.assistant.identity.user.entity.GenderType;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;

import com.hubt.assistant.storage.service.LocalFileStorageService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.Instant;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final LocalFileStorageService fileStorageService;

    private final UserRepository userRepository;

    private final CandidateProfileRepository candidateProfileRepository;

    private final CandidateCodeGenerator candidateCodeGenerator;


    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @Transactional
    public CandidateProfileResponse getMyProfile(
            UUID userId
    ) {

        User user =
                getUser(userId);

        CandidateProfile profile =
                getProfile(user);

        return toResponse(
                user,
                profile
        );
    }


    // =========================================================
    // UPDATE MY PROFILE
    // =========================================================

    @Transactional
    public CandidateProfileResponse updateMyProfile(
            UUID userId,
            UpdateCandidateProfileRequest request
    ) {

        User user =
                getUser(userId);

        CandidateProfile profile =
                getProfile(user);


        // =====================================================
        // USER INFORMATION
        // =====================================================

        if (request.fullName() != null) {

            String fullName =
                    request.fullName()
                            .trim();

            if (fullName.isBlank()) {

                throw new BusinessException(
                        "FULL_NAME_INVALID",
                        "Họ tên không được để trống"
                );
            }

            user.setFullName(
                    fullName
            );
        }


        if (request.phone() != null) {

    String phone =
            normalizePhone(
                    request.phone()
            );

    if (phone != null
            && userRepository
            .existsByPhoneAndIdNot(
                    phone,
                    userId
            )) {

        throw new BusinessException(
                "PHONE_ALREADY_EXISTS",
                "Số điện thoại đã được sử dụng"
        );
    }

    user.setPhone(
            phone
    );
}


        if (request.dateOfBirth() != null) {

    LocalDate dateOfBirth =
            request.dateOfBirth();

    LocalDate today =
            LocalDate.now();

    if (dateOfBirth.isAfter(today)) {

        throw new BusinessException(
                "DATE_OF_BIRTH_INVALID",
                "Ngày sinh không được lớn hơn ngày hiện tại"
        );
    }

    if (dateOfBirth.isBefore(
            today.minusYears(100)
    )) {

        throw new BusinessException(
                "DATE_OF_BIRTH_INVALID",
                "Ngày sinh không hợp lệ"
        );
    }

    if (dateOfBirth.isAfter(
            today.minusYears(10)
    )) {

        throw new BusinessException(
                "AGE_INVALID",
                "Tuổi thí sinh không hợp lệ"
        );
    }

    user.setDateOfBirth(
            dateOfBirth
    );
}


        if (request.gender() != null) {

            try {

                user.setGender(
                        GenderType.valueOf(
                                request.gender()
                                        .trim()
                                        .toUpperCase()
                        )
                );

            } catch (IllegalArgumentException ex) {

                throw new BusinessException(
                        "GENDER_INVALID",
                        "Giới tính không hợp lệ"
                );
            }
        }


        // =====================================================
        // CANDIDATE INFORMATION
        // =====================================================

        if (request.identityNumber() != null) {

    String identityNumber =
            normalizeNullable(
                    request.identityNumber()
            );

    if (identityNumber != null) {

        identityNumber =
                identityNumber.replaceAll(
                        "\\s+",
                        ""
                );

        if (!identityNumber.matches(
                "^[0-9]{9,12}$"
        )) {

            throw new BusinessException(
                    "IDENTITY_NUMBER_INVALID",
                    "CCCD/CMND phải gồm từ 9 đến 12 chữ số"
            );
        }

        if (candidateProfileRepository
                .existsByIdentityNumberAndUserIdNot(
                        identityNumber,
                        userId
                )) {

            throw new BusinessException(
                    "IDENTITY_NUMBER_ALREADY_EXISTS",
                    "Số CCCD/CMND đã được sử dụng"
            );
        }
    }

    profile.setIdentityNumber(
            identityNumber
    );
}


        if (request.schoolName() != null) {

            profile.setSchoolName(
                    normalizeNullable(
                            request.schoolName()
                    )
            );
        }


        if (request.provinceCode() != null) {

            profile.setProvinceCode(
                    normalizeNullable(
                            request.provinceCode()
                    )
            );
        }


        if (request.districtCode() != null) {

            profile.setDistrictCode(
                    normalizeNullable(
                            request.districtCode()
                    )
            );
        }


        if (request.graduationYear() != null) {

    int graduationYear =
            request.graduationYear();

    int currentYear =
            LocalDate.now().getYear();

    if (graduationYear
            > currentYear + 1) {

        throw new BusinessException(
                "GRADUATION_YEAR_INVALID",
                "Năm tốt nghiệp không hợp lệ"
        );
    }

    if (user.getDateOfBirth() != null) {

        int birthYear =
                user.getDateOfBirth()
                        .getYear();

        if (graduationYear
                < birthYear + 14) {

            throw new BusinessException(
                    "GRADUATION_YEAR_INVALID",
                    "Năm tốt nghiệp không phù hợp với ngày sinh"
            );
        }
    }

    profile.setGraduationYear(
            graduationYear
    );
}


        if (request.educationLevel() != null) {

            profile.setEducationLevel(
                    normalizeNullable(
                            request.educationLevel()
                    )
            );
        }


        if (request.careerGoal() != null) {

            profile.setCareerGoal(
                    normalizeNullable(
                            request.careerGoal()
                    )
            );
        }


        if (request.preferredStudyLocation()
                != null) {

            profile.setPreferredStudyLocation(
                    normalizeNullable(
                            request.preferredStudyLocation()
                    )
            );
        }


        // =====================================================
        // PROFILE COMPLETION
        // =====================================================

        ProfileCompletionResponse completion =
                calculateProfileCompletion(
                        user,
                        profile
                );

        profile.setProfileCompletionPercent(
                completion.completionPercent()
        );


        userRepository.save(
                user
        );

        CandidateProfile savedProfile =
                candidateProfileRepository.save(
                        profile
                );


        return toResponse(
                user,
                savedProfile
        );
    }


    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(
            UUID userId
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "USER_NOT_FOUND",
                                                "Không tìm thấy người dùng"
                                        )
                        );


        if (user.getDeletedAt() != null
                || user.getAccountStatus()
                == AccountStatus.DELETED) {

            throw new ResourceNotFoundException(
                    "USER_NOT_FOUND",
                    "Không tìm thấy người dùng"
            );
        }

        return user;
    }


    // =========================================================
    // GET PROFILE
    // =========================================================

    private CandidateProfile getProfile(
            User user
    ) {

        CandidateProfile profile =
                candidateProfileRepository
                        .findByUserId(
                                user.getId()
                        )
                        .orElseGet(
                                () ->
                                        createProfile(
                                                user
                                        )
                        );


        if (profile.getCandidateCode() == null
                || profile.getCandidateCode()
                .isBlank()) {

            profile.setCandidateCode(
                    candidateCodeGenerator
                            .generate()
            );

            profile =
                    candidateProfileRepository
                            .save(profile);
        }

        return profile;
    }


    // =========================================================
    // CREATE PROFILE
    // =========================================================

    private CandidateProfile createProfile(
            User user
    ) {

        CandidateProfile profile =
                new CandidateProfile();

        profile.setUser(
                user
        );

        profile.setCandidateCode(
                candidateCodeGenerator
                        .generate()
        );

        profile.setProfileCompletionPercent(
                BigDecimal.ZERO
        );

        profile.setCreatedAt(
                Instant.now()
        );

        profile.setUpdatedAt(
                Instant.now()
        );

        return candidateProfileRepository
                .save(profile);
    }


    // =========================================================
    // CALCULATE PROFILE COMPLETION
    // =========================================================

    private ProfileCompletionResponse
    calculateProfileCompletion(
            User user,
            CandidateProfile profile
    ) {

        List<String> missingFields =
                new ArrayList<>();

        int completed = 0;

        int total = 12;


        // =====================================================
        // USER FIELDS
        // =====================================================

        if (hasText(
                user.getFullName()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "FULL_NAME"
            );
        }


        if (hasText(
                user.getEmail()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "EMAIL"
            );
        }


        if (hasText(
                user.getPhone()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "PHONE"
            );
        }


        if (user.getDateOfBirth()
                != null) {

            completed++;

        } else {

            missingFields.add(
                    "DATE_OF_BIRTH"
            );
        }


        if (user.getGender() != null
                && user.getGender()
                != GenderType.UNDISCLOSED) {

            completed++;

        } else {

            missingFields.add(
                    "GENDER"
            );
        }


        // =====================================================
        // CANDIDATE PROFILE FIELDS
        // =====================================================

        if (hasText(
                profile.getIdentityNumber()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "IDENTITY_NUMBER"
            );
        }


        if (hasText(
                profile.getSchoolName()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "SCHOOL_NAME"
            );
        }


        if (hasText(
                profile.getProvinceCode()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "PROVINCE_CODE"
            );
        }


        if (profile.getGraduationYear()
                != null) {

            completed++;

        } else {

            missingFields.add(
                    "GRADUATION_YEAR"
            );
        }


        if (hasText(
                profile.getEducationLevel()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "EDUCATION_LEVEL"
            );
        }


        if (hasText(
                profile.getCareerGoal()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "CAREER_GOAL"
            );
        }


        if (hasText(
                profile.getPreferredStudyLocation()
        )) {

            completed++;

        } else {

            missingFields.add(
                    "PREFERRED_STUDY_LOCATION"
            );
        }


        BigDecimal percent =
                BigDecimal
                        .valueOf(completed)
                        .multiply(
                                BigDecimal.valueOf(
                                        100
                                )
                        )
                        .divide(
                                BigDecimal.valueOf(
                                        total
                                ),
                                2,
                                RoundingMode.HALF_UP
                        );


        boolean completedProfile =
                completed == total;


        return new ProfileCompletionResponse(
                percent,
                completedProfile,
                completed,
                total,
                List.copyOf(
                        missingFields
                )
        );
    }


    // =========================================================
    // MAPPER
    // =========================================================

    private CandidateProfileResponse toResponse(
            User user,
            CandidateProfile profile
    ) {

        ProfileCompletionResponse completion =
                calculateProfileCompletion(
                        user,
                        profile
                );


        return new CandidateProfileResponse(

                user.getId(),

                profile.getCandidateCode(),

                user.getFullName(),

                user.getEmail(),

                user.getPhone(),

                user.getDateOfBirth(),

                user.getGender() == null
                        ? null
                        : user.getGender()
                        .name(),

                user.getAvatarUrl(),

                user.getAccountStatus()
                        .name(),

                user.isEmailVerified(),

                user.isPhoneVerified(),

                profile.getIdentityNumber(),

                profile.getSchoolName(),

                profile.getProvinceCode(),

                profile.getDistrictCode(),

                profile.getGraduationYear(),

                profile.getEducationLevel(),

                profile.getCareerGoal(),

                profile.getPreferredStudyLocation(),

                completion.completionPercent(),

                completion.completed(),

                completion.missingFields(),

                profile.getCreatedAt(),

                profile.getUpdatedAt()
        );
    }


    // =========================================================
    // UPDATE AVATAR
    // =========================================================

    @Transactional
    public AvatarUploadResponse updateAvatar(
            UUID userId,
            MultipartFile file
    ) {

        User user =
                getUser(userId);

        String oldAvatarUrl =
                user.getAvatarUrl();


        String newAvatarUrl =
                fileStorageService
                        .storeAvatar(file);


        user.setAvatarUrl(
                newAvatarUrl
        );

        userRepository.save(
                user
        );


        if (oldAvatarUrl != null
                && !oldAvatarUrl.equals(
                        newAvatarUrl
                )) {

            fileStorageService
                    .deleteAvatar(
                            oldAvatarUrl
                    );
        }


        return new AvatarUploadResponse(
                newAvatarUrl
        );
    }


    // =========================================================
    // GET PROFILE COMPLETION
    // =========================================================

    @Transactional
    public ProfileCompletionResponse
    getProfileCompletion(
            UUID userId
    ) {

        User user =
                getUser(userId);

        CandidateProfile profile =
                getProfile(user);


        ProfileCompletionResponse completion =
                calculateProfileCompletion(
                        user,
                        profile
                );


        profile.setProfileCompletionPercent(
                completion.completionPercent()
        );

        candidateProfileRepository.save(
                profile
        );


        return completion;
    }


    // =========================================================
    // NORMALIZE STRING
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


    // =========================================================
    // HAS TEXT
    // =========================================================

    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.isBlank();
    }
    private String normalizePhone(
        String value
) {

    if (value == null) {
        return null;
    }

    String phone =
            value
                    .trim()
                    .replaceAll(
                            "[\\s.-]",
                            ""
                    );

    if (phone.isBlank()) {
        return null;
    }

    if (phone.startsWith("+84")) {

        phone =
                "0"
                + phone.substring(3);
    }

    if (!phone.matches(
            "^0[0-9]{9}$"
    )) {

        throw new BusinessException(
                "PHONE_INVALID",
                "Số điện thoại không hợp lệ"
        );
    }

    return phone;
}
}