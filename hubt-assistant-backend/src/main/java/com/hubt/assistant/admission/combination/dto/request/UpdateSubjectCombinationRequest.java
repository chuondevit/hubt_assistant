package com.hubt.assistant.admission.combination.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateSubjectCombinationRequest(

        @Size(
                min = 1,
                max = 30
        )
        String code,

        @Size(
                min = 1,
                max = 255
        )
        String name,

        String description,

        List<@Valid SubjectCombinationItemRequest> items

) {
}