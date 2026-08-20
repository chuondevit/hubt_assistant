package com.hubt.assistant.admission.round.specification;

import com.hubt.assistant.admission.round.entity.AdmissionRound;
import com.hubt.assistant.admission.round.entity.AdmissionRoundStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public final class AdmissionRoundSpecification {

    private AdmissionRoundSpecification() {
    }


    public static Specification<AdmissionRound>
    hasAdmissionYearId(
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


    public static Specification<AdmissionRound>
    hasUniversityId(
            UUID universityId
    ) {

        return (root, query, cb) -> {

            if (universityId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionYear")
                            .get("university")
                            .get("id"),
                    universityId
            );
        };
    }


    public static Specification<AdmissionRound>
    hasStatus(
            AdmissionRoundStatus status
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


    public static Specification<AdmissionRound>
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
                    + keyword.trim().toLowerCase()
                    + "%";

            return cb.like(
                    cb.lower(
                            root.get("name")
                    ),
                    value
            );
        };
    }
}