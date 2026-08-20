package com.hubt.assistant.organization.program.repository;

import com.hubt.assistant.organization.program.entity.Program;
import com.hubt.assistant.organization.program.entity.ProgramStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgramRepository
        extends JpaRepository<Program, UUID>,
        JpaSpecificationExecutor<Program> {

    Optional<Program> findById(UUID id);

    boolean existsByMajor_IdAndCodeIgnoreCase(
            UUID majorId,
            String code
    );

    boolean existsByMajor_IdAndCodeIgnoreCaseAndIdNot(
            UUID majorId,
            String code,
            UUID id
    );

    List<Program>
    findAllByMajor_IdAndStatusOrderByNameAsc(
            UUID majorId,
            ProgramStatus status
    );

    List<Program>
    findAllByStatusOrderByNameAsc(
            ProgramStatus status
    );

    Optional<Program>
    findByMajor_IdAndCodeIgnoreCase(
            UUID majorId,
            String code
    );
}