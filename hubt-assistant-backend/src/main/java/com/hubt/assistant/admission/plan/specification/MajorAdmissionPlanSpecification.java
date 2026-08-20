package com.hubt.assistant.admission.plan.specification;

import com.hubt.assistant.admission.plan.entity.MajorAdmissionPlan;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class MajorAdmissionPlanSpecification {

    private MajorAdmissionPlanSpecification() {
    }


    public static Specification<MajorAdmissionPlan> hasAdmissionYear(
            UUID admissionYearId
    ) {

        return (root, query, cb) -> {

            if (admissionYearId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionYear").get("id"),
                    admissionYearId
            );
        };
    }


    public static Specification<MajorAdmissionPlan> hasMajor(
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


    public static Specification<MajorAdmissionPlan> hasProgram(
            UUID programId
    ) {

        return (root, query, cb) -> {

            if (programId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("program").get("id"),
                    programId
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
}