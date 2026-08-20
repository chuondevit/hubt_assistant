package com.hubt.assistant.admission.combination.repository;

import com.hubt.assistant.admission.combination.entity.SubjectCombination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SubjectCombinationRepository
        extends JpaRepository<SubjectCombination, UUID>,
        JpaSpecificationExecutor<SubjectCombination> {

    boolean existsByCodeIgnoreCase(
            String code
    );

    boolean existsByCodeIgnoreCaseAndIdNot(
            String code,
            UUID id
    );

    List<SubjectCombination> findAllByOrderByCodeAsc();
}