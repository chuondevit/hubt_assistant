package com.hubt.assistant.identity.profile.specification;

import com.hubt.assistant.identity.profile.dto.request.AdminCandidateFilter;
import com.hubt.assistant.identity.profile.entity.CandidateProfile;

import com.hubt.assistant.identity.user.entity.AccountStatus;
import com.hubt.assistant.identity.user.entity.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public final class CandidateProfileSpecification {

    private CandidateProfileSpecification() {
    }


    public static Specification<CandidateProfile> filter(
            AdminCandidateFilter filter
    ) {

        return (
                root,
                query,
                criteriaBuilder
        ) -> {

            List<Predicate> predicates =
                    new ArrayList<>();


            // =================================================
            // JOIN USER
            // =================================================

            Join<CandidateProfile, User> user =
                    root.join(
                            "user",
                            JoinType.INNER
                    );


            // Tránh record duplicate nếu sau này thêm join khác
            query.distinct(true);


            // =================================================
            // KEYWORD
            //
            // Search:
            // candidateCode
            // fullName
            // email
            // phone
            // =================================================

            if (filter.keyword() != null
                    && !filter.keyword()
                    .isBlank()) {

                String keyword =
                        "%"
                                + filter.keyword()
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                                + "%";


                Predicate candidateCode =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get(
                                                "candidateCode"
                                        )
                                ),
                                keyword
                        );


                Predicate fullName =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        user.get(
                                                "fullName"
                                        )
                                ),
                                keyword
                        );


                Predicate email =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        user.get(
                                                "email"
                                        )
                                ),
                                keyword
                        );


                Predicate phone =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        user.get(
                                                "phone"
                                        )
                                ),
                                keyword
                        );


                predicates.add(
                        criteriaBuilder.or(
                                candidateCode,
                                fullName,
                                email,
                                phone
                        )
                );
            }


            // =================================================
            // ACCOUNT STATUS
            // =================================================

            if (filter.status() != null
                    && !filter.status()
                    .isBlank()) {

                try {

                    AccountStatus accountStatus =
                            AccountStatus.valueOf(
                                    filter.status()
                                            .trim()
                                            .toUpperCase(
                                                    Locale.ROOT
                                            )
                            );


                    predicates.add(
                            criteriaBuilder.equal(
                                    user.get(
                                            "accountStatus"
                                    ),
                                    accountStatus
                            )
                    );

                } catch (
                        IllegalArgumentException ex
                ) {

                    // Service sẽ validate trước.
                    // Đây chỉ là lớp bảo vệ phụ.
                    predicates.add(
                            criteriaBuilder.disjunction()
                    );
                }
            }


            // =================================================
            // PROFILE COMPLETED
            //
            // 100 = completed
            // < 100 = incomplete
            // =================================================

            if (filter.profileCompleted()
                    != null) {

                if (Boolean.TRUE.equals(
                        filter.profileCompleted()
                )) {

                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get(
                                            "profileCompletionPercent"
                                    ),
                                    new BigDecimal(
                                            "100"
                                    )
                            )
                    );

                } else {

                    predicates.add(
                            criteriaBuilder.or(

                                    criteriaBuilder.isNull(
                                            root.get(
                                                    "profileCompletionPercent"
                                            )
                                    ),

                                    criteriaBuilder.lessThan(
                                            root.get(
                                                    "profileCompletionPercent"
                                            ),
                                            new BigDecimal(
                                                    "100"
                                            )
                                    )
                            )
                    );
                }
            }


            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }
}