package com.hubt.assistant.organization.faculty.repository;

import com.hubt.assistant.organization.faculty.entity.Faculty;
import com.hubt.assistant.organization.faculty.entity.FacultyStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FacultyRepository
        extends JpaRepository<Faculty, UUID>,
        JpaSpecificationExecutor<Faculty> {


    Optional<Faculty>
    findById(
            UUID id
    );


    boolean
    existsByUniversityIdAndCodeIgnoreCase(
            UUID universityId,
            String code
    );


    boolean
    existsByUniversityIdAndCodeIgnoreCaseAndIdNot(
            UUID universityId,
            String code,
            UUID id
    );


    List<Faculty>
    findAllByUniversityIdAndStatusOrderByNameAsc(
            UUID universityId,
            FacultyStatus status
    );


    List<Faculty>
    findAllByStatusOrderByNameAsc(
            FacultyStatus status
    );
}