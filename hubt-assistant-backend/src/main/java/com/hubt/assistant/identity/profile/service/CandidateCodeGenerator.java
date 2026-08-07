package com.hubt.assistant.identity.profile.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class CandidateCodeGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    public String generate() {

        Number sequenceValue =
                (Number) entityManager
                        .createNativeQuery(
                                "SELECT nextval('hubt.candidate_code_seq')"
                        )
                        .getSingleResult();

        long number =
                sequenceValue.longValue();

        int year =
                Year.now().getValue();

        return String.format(
                "TS%d%05d",
                year,
                number
        );
    }
}