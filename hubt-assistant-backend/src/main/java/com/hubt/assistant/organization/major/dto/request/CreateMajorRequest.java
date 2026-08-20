package com.hubt.assistant.organization.major.dto.request;

import com.hubt.assistant.organization.major.entity.DegreeLevel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;


public record CreateMajorRequest(

        @NotNull(
                message = "University ID không được để trống"
        )
        UUID universityId,


        UUID facultyId,


        @NotBlank(
                message = "Mã ngành không được để trống"
        )
        @Size(
                max = 50,
                message = "Mã ngành không được vượt quá 50 ký tự"
        )
        String code,


        @NotBlank(
                message = "Tên ngành không được để trống"
        )
        @Size(
                max = 255,
                message = "Tên ngành không được vượt quá 255 ký tự"
        )
        String name,


        @NotNull(
                message = "Bậc đào tạo không được để trống"
        )
        DegreeLevel degreeLevel,


        @DecimalMin(
                value = "0.1",
                message = "Thời gian đào tạo phải lớn hơn 0"
        )
        BigDecimal durationYears,


        String description,

        String learningOutcomes,

        String careerOpportunities,

        String requiredSkills,

        String thumbnailUrl

) {
}