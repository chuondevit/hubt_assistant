package com.hubt.assistant.admission.combination.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record SubjectCombinationItemResponse(

        UUID subjectId,

        String subjectCode,

        String subjectName,

        BigDecimal coefficient

) {
}