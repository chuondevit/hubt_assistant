package com.hubt.assistant.organization.program.specification;

import com.hubt.assistant.organization.program.entity.Program;
import com.hubt.assistant.organization.program.entity.ProgramStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class ProgramSpecification {

    private ProgramSpecification() {
    }

    public static Specification<Program> hasKeyword(
            String keyword
    ) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value =
                    "%" + keyword.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("code")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("name")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("trainingMode")),
                            value
                    ),
                    cb.like(
                            cb.lower(root.get("language")),
                            value
                    )
            );
        };
    }

    public static Specification<Program> hasMajorId(
            UUID majorId
    ) {

        return (root, query, cb) -> {

            if (majorId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("major").get("id"),
                    majorId
            );
        };
    }

    public static Specification<Program> hasUniversityId(
            UUID universityId
    ) {

        return (root, query, cb) -> {

            if (universityId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("major")
                            .get("university")
                            .get("id"),
                    universityId
            );
        };
    }

    public static Specification<Program> hasFacultyId(
            UUID facultyId
    ) {

        return (root, query, cb) -> {

            if (facultyId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("major")
                            .get("faculty")
                            .get("id"),
                    facultyId
            );
        };
    }

    public static Specification<Program> hasStatus(
            ProgramStatus status
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