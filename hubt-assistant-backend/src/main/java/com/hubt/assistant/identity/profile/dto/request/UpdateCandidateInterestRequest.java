package com.hubt.assistant.identity.profile.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCandidateInterestRequest(

        @Size(
                min = 1,
                max = 255,
                message = "Tên sở thích phải từ 1 đến 255 ký tự"
        )
        String interestName,

        @Pattern(
                regexp = "^(LOW|MEDIUM|HIGH|VERY_HIGH)$",
                message = "Mức độ quan tâm không hợp lệ"
        )
        String level,

        @Size(
                max = 100,
                message = "Nguồn dữ liệu không được vượt quá 100 ký tự"
        )
        String source

) {
}