package com.hubt.assistant.identity.profile.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProfileCompletionResponse(

        BigDecimal completionPercent,

        boolean completed,

        int completedFields,

        int totalFields,

        List<String> missingFields

) {
}