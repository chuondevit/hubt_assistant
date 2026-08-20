package com.hubt.assistant.organization.faculty.specification;

import com.hubt.assistant.organization.faculty.entity.Faculty;
import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class FacultySpecification {

    private FacultySpecification() {
    }

    // =========================================================
    // KEYWORD
    // Search: code + name + deanName
    // =========================================================

    public static Specification<Faculty> hasKeyword(
            String keyword
    ) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value =
                    "%"
                    + keyword.trim().toLowerCase()
                    + "%";

            return cb.or(

                    cb.like(
                            cb.lower(
                                    root.get("code")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("name")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("deanName")
                            ),
                            value
                    )
            );
        };
    }


    // =========================================================
    // UNIVERSITY
    // =========================================================

    public static Specification<Faculty> hasUniversityId(
            UUID universityId
    ) {

        return (root, query, cb) -> {

            if (universityId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("university")
                            .get("id"),
                    universityId
            );
        };
    }


    // =========================================================
    // STATUS
    // =========================================================

    public static Specification<Faculty> hasStatus(
            FacultyStatus status
    ) {

        return (root, query, cb) -> {

            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("status"),
                    status
            );
        };
    }
}