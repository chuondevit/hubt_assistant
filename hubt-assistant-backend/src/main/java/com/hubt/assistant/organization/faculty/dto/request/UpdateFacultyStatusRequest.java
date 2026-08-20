package com.hubt.assistant.organization.faculty.dto.request;

import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import jakarta.validation.constraints.NotNull;


public record UpdateFacultyStatusRequest(

        @NotNull(
                message = "Trạng thái khoa không được để trống"
        )
        FacultyStatus status

) {
}