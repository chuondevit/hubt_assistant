package com.hubt.assistant.identity.profile.dto.request;

public record AdminCandidateFilter(

        String keyword,

        String status,

        Boolean profileCompleted

) {
}