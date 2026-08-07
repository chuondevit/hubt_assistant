package com.hubt.assistant.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(

        boolean success,

        String code,

        String message,

        Map<String, String> errors,

        Instant timestamp

) {
}