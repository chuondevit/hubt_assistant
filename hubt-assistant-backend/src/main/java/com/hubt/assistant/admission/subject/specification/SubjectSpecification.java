package com.hubt.assistant.admission.subject.specification;

import com.hubt.assistant.admission.subject.entity.Subject;

import org.springframework.data.jpa.domain.Specification;


public final class SubjectSpecification {

    private SubjectSpecification() {
    }


    public static Specification<Subject>
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
}