package com.hubt.assistant.common.api;
import com.hubt.assistant.common.api.PageResponse;

import com.hubt.assistant.identity.profile.dto.request.AdminCandidateFilter;

import com.hubt.assistant.identity.profile.specification.CandidateProfileSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(

        List<T> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean first,

        boolean last,

        boolean empty

) {

    public static <T> PageResponse<T> from(
            Page<T> page
    ) {

        return new PageResponse<>(

                page.getContent(),

                page.getNumber(),

                page.getSize(),

                page.getTotalElements(),

                page.getTotalPages(),

                page.isFirst(),

                page.isLast(),

                page.isEmpty()
        );
    }
}