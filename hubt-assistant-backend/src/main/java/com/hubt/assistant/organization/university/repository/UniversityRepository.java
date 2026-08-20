package com.hubt.assistant.organization.university.repository;

import com.hubt.assistant.organization.university.entity.University;
import com.hubt.assistant.organization.university.entity.UniversityStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UniversityRepository
        extends JpaRepository<University, UUID>,
        JpaSpecificationExecutor<University> {

    Optional<University>
    findByCodeAndDeletedAtIsNull(
            String code
    );

    Optional<University>
    findByIdAndDeletedAtIsNull(
            UUID id
    );

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(
            String code
    );

    boolean existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(
            String code,
            UUID id
    );

    List<University>
    findAllByStatusAndDeletedAtIsNullOrderByNameAsc(
            UniversityStatus status
    );
}