package com.hubt.assistant.admission.combination.dto.response;

import java.util.List;
import java.util.UUID;

public record SubjectCombinationResponse(

        UUID id,

        String code,

        String name,

        String description,

        List<SubjectCombinationItemResponse> items

) {
}