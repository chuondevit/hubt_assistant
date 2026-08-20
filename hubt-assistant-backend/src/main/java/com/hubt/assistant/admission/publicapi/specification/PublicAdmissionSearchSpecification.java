package com.hubt.assistant.admission.publicapi.specification;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;

import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public final class PublicAdmissionSearchSpecification {

    private PublicAdmissionSearchSpecification() {
    }


    public static Specification<MajorAdmissionPlan> hasKeyword(
            String keyword
    ) {

        return (root, query, cb) -> {

            if (keyword == null
                    || keyword.isBlank()) {

                return cb.conjunction();
            }

            String value =
                    "%"
                    + keyword.trim().toLowerCase()
                    + "%";

            return cb.or(

                    cb.like(
                            cb.lower(
                                    root.get("major")
                                            .get("code")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("major")
                                            .get("name")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("program")
                                            .get("code")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("program")
                                            .get("name")
                            ),
                            value
                    )
            );
        };
    }


    public static Specification<MajorAdmissionPlan> hasUniversityId(
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


    public static Specification<MajorAdmissionPlan> hasAdmissionYearId(
            UUID admissionYearId
    ) {

        return (root, query, cb) -> {

            if (admissionYearId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionYear")
                            .get("id"),
                    admissionYearId
            );
        };
    }


    public static Specification<MajorAdmissionPlan> hasApplicationOpen(
            Boolean applicationOpen
    ) {

        return (root, query, cb) -> {

            if (applicationOpen == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("applicationOpen"),
                    applicationOpen
            );
        };
    }


    public static Specification<MajorAdmissionPlan> hasAdmissionMethodId(
            UUID admissionMethodId
    ) {

        return (root, query, cb) -> {

            if (admissionMethodId == null) {
                return cb.conjunction();
            }

            query.distinct(true);

            var methods =
                    root.join(
                            "majorAdmissionMethods",
                            JoinType.INNER
                    );

            return cb.equal(
                    methods.get("admissionMethod")
                            .get("id"),
                    admissionMethodId
            );
        };
    }


    public static Specification<MajorAdmissionPlan> hasSubjectComboId(
            UUID subjectComboId
    ) {

        return (root, query, cb) -> {

            if (subjectComboId == null) {
                return cb.conjunction();
            }

            query.distinct(true);

            var methods =
                    root.join(
                            "majorAdmissionMethods",
                            JoinType.INNER
                    );

            var combos =
                    methods.join(
                            "subjectCombinations",
                            JoinType.INNER
                    );

            return cb.equal(
                    combos.get("subjectCombination")
                            .get("id"),
                    subjectComboId
            );
        };
    }


    public static Specification<MajorAdmissionPlan> minExpectedCutoff(
            BigDecimal minCutoff
    ) {

        return (root, query, cb) -> {

            if (minCutoff == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("expectedCutoff"),
                    minCutoff
            );
        };
    }


    public static Specification<MajorAdmissionPlan> maxExpectedCutoff(
            BigDecimal maxCutoff
    ) {

        return (root, query, cb) -> {

            if (maxCutoff == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("expectedCutoff"),
                    maxCutoff
            );
        };
    }
}