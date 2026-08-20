package com.hubt.assistant.organization.major.dto.request;

import com.hubt.assistant.organization.major.entity.DegreeLevel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;


public record UpdateMajorRequest(

        UUID universityId,

        UUID facultyId,


        @Size(
                min = 1,
                max = 50,
                message = "Mã ngành phải từ 1 đến 50 ký tự"
        )
        String code,


        @Size(
                min = 1,
                max = 255,
                message = "Tên ngành phải từ 1 đến 255 ký tự"
        )
        String name,


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