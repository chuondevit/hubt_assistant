package com.hubt.assistant.organization.major.specification;

import com.hubt.assistant.organization.major.entity.DegreeLevel;
import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.entity.MajorStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public final class MajorSpecification {

    private MajorSpecification() {
    }


    public static Specification<Major>
    notDeleted() {

        return (root, query, cb) ->
                cb.isNull(
                        root.get("deletedAt")
                );
    }


    public static Specification<Major>
    hasKeyword(
            String keyword
    ) {

        return (root, query, cb) -> {

            if (keyword == null
                    || keyword.isBlank()) {

                return cb.conjunction();
            }


            String value =
                    "%"
                            + keyword
                            .trim()
                            .toLowerCase()
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
                    )
            );
        };
    }


    public static Specification<Major>
    hasUniversityId(
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


    public static Specification<Major>
    hasFacultyId(
            UUID facultyId
    ) {

        return (root, query, cb) -> {

            if (facultyId == null) {
                return cb.conjunction();
            }


            return cb.equal(
                    root.get("faculty")
                            .get("id"),
                    facultyId
            );
        };
    }


    public static Specification<Major>
    hasDegreeLevel(
            DegreeLevel degreeLevel
    ) {

        return (root, query, cb) -> {

            if (degreeLevel == null) {
                return cb.conjunction();
            }


            return cb.equal(
                    root.get("degreeLevel"),
                    degreeLevel
            );
        };
    }


    public static Specification<Major>
    hasStatus(
            MajorStatus status
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