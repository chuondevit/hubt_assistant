package com.hubt.assistant.admission.method.specification;

import com.hubt.assistant.admission.method.entity.AdmissionMethod;
import com.hubt.assistant.admission.method.entity.AdmissionMethodStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public final class AdmissionMethodSpecification {

    private AdmissionMethodSpecification() {
    }


    public static Specification<AdmissionMethod>
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


    public static Specification<AdmissionMethod>
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


    public static Specification<AdmissionMethod>
    hasStatus(
            AdmissionMethodStatus status
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