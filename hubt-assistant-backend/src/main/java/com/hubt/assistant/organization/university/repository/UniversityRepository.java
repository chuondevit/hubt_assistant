package com.hubt.assistant.organization.university.repository;

import com.hubt.assistant.organization.university.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UniversityRepository
        extends JpaRepository<University, UUID> {

    Optional<University> findByCodeAndDeletedAtIsNull(String code);
}