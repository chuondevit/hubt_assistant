package com.hubt.assistant.admission.cutoff.specification;

import com.hubt.assistant.admission.cutoff.entity.MajorCutoff;
import com.hubt.assistant.admission.cutoff.entity.MajorCutoffStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class MajorCutoffSpecification {

    private MajorCutoffSpecification() {
    }

    public static Specification<MajorCutoff> hasAdmissionYearId(
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

    public static Specification<MajorCutoff> hasAdmissionRoundId(
            UUID admissionRoundId
    ) {

        return (root, query, cb) -> {

            if (admissionRoundId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionRound").get("id"),
                    admissionRoundId
            );
        };
    }

    public static Specification<MajorCutoff> hasMajorId(
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

    public static Specification<MajorCutoff> hasAdmissionMethodId(
            UUID admissionMethodId
    ) {

        return (root, query, cb) -> {

            if (admissionMethodId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("admissionMethod").get("id"),
                    admissionMethodId
            );
        };
    }

    public static Specification<MajorCutoff> hasSubjectComboId(
            UUID subjectComboId
    ) {

        return (root, query, cb) -> {

            if (subjectComboId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("subjectCombination").get("id"),
                    subjectComboId
            );
        };
    }

    public static Specification<MajorCutoff> hasStatus(
            MajorCutoffStatus status
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