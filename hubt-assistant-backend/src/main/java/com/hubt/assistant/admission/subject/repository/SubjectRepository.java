package com.hubt.assistant.admission.subject.repository;

import com.hubt.assistant.admission.subject.entity.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SubjectRepository
        extends JpaRepository<Subject, UUID>,
        JpaSpecificationExecutor<Subject> {


    Optional<Subject>
    findById(UUID id);


    Optional<Subject>
    findByCodeIgnoreCase(
            String code
    );


    boolean
    existsByCodeIgnoreCase(
            String code
    );


    boolean
    existsByCodeIgnoreCaseAndIdNot(
            String code,
            UUID id
    );


    List<Subject>
    findAllByOrderByNameAsc();
}