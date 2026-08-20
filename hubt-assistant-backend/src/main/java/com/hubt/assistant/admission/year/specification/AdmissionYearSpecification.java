package com.hubt.assistant.admission.year.specification;

import com.hubt.assistant.admission.year.entity.AdmissionYear;
import com.hubt.assistant.admission.year.entity.AdmissionYearStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public final class AdmissionYearSpecification {

    private AdmissionYearSpecification() {
    }


    public static Specification<AdmissionYear>
    hasUniversityId(
            UUID universityId
    ) {

        return (root, query, cb) -> {

            if (universityId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("university").get("id"),
                    universityId
            );
        };
    }


    public static Specification<AdmissionYear>
    hasYear(
            Integer year
    ) {

        return (root, query, cb) -> {

            if (year == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("year"),
                    year
            );
        };
    }


    public static Specification<AdmissionYear>
    hasStatus(
            AdmissionYearStatus status
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