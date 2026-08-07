package com.hubt.assistant.identity.profile.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCandidateProfileRequest(

        @Size(
                min = 2,
                max = 255,
                message = "Họ tên phải từ 2 đến 255 ký tự"
        )
        String fullName,


        @Pattern(
                regexp = "^$|^(0|\\+84)[0-9]{9}$",
                message = "Số điện thoại không hợp lệ"
        )
        String phone,


        LocalDate dateOfBirth,


        @Pattern(
                regexp = "^(MALE|FEMALE|OTHER|UNDISCLOSED)$",
                message = "Giới tính không hợp lệ"
        )
        String gender,


        @Pattern(
                regexp = "^$|^[0-9]{9,12}$",
                message = "CCCD/CMND phải gồm từ 9 đến 12 chữ số"
        )
        String identityNumber,


        @Size(
                max = 255,
                message = "Tên trường không được vượt quá 255 ký tự"
        )
        String schoolName,


        @Size(
                max = 30,
                message = "Mã tỉnh không được vượt quá 30 ký tự"
        )
        String provinceCode,


        @Size(
                max = 30,
                message = "Mã quận/huyện không được vượt quá 30 ký tự"
        )
        String districtCode,


        @Min(
                value = 1990,
                message = "Năm tốt nghiệp không hợp lệ"
        )
        @Max(
                value = 2100,
                message = "Năm tốt nghiệp không hợp lệ"
        )
        Integer graduationYear,


        @Size(
                max = 100,
                message = "Trình độ học vấn không được vượt quá 100 ký tự"
        )
        String educationLevel,


        @Size(
                max = 2000,
                message = "Mục tiêu nghề nghiệp không được vượt quá 2000 ký tự"
        )
        String careerGoal,


        @Size(
                max = 255,
                message = "Địa điểm học mong muốn không được vượt quá 255 ký tự"
        )
        String preferredStudyLocation

) {
}