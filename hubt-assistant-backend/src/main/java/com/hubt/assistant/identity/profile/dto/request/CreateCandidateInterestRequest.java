package com.hubt.assistant.identity.profile.dto.request;


import jakarta.validation.constraints.NotBlank;


public record CreateCandidateInterestRequest(

        @NotBlank
        String interestCode,


        @NotBlank
        String interestName,


        @NotBlank
        String level,


        String source

) {}