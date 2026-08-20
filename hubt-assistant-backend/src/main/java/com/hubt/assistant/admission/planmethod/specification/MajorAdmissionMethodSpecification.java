package com.hubt.assistant.admission.planmethod.specification;

import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethod;
import com.hubt.assistant.admission.planmethod.entity.MajorAdmissionMethodStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class MajorAdmissionMethodSpecification {

    private MajorAdmissionMethodSpecification() {
    }

    public static Specification<MajorAdmissionMethod>
    hasPlanId(
            UUID planId
    ) {

        return (root, query, cb) -> {

            if (planId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("majorAdmissionPlan")
                            .get("id"),
                    planId
            );
        };
    }

    public static Specification<MajorAdmissionMethod>
    hasAdmissionMethodId(
            UUID methodId
    ) {

        return (root, query, cb) -> {

            if (methodId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionMethod")
                            .get("id"),
                    methodId
            );
        };
    }

    public static Specification<MajorAdmissionMethod>
    hasStatus(
            MajorAdmissionMethodStatus status
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