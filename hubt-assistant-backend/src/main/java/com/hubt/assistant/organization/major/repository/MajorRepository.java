package com.hubt.assistant.organization.major.repository;

import com.hubt.assistant.organization.major.entity.Major;
import com.hubt.assistant.organization.major.entity.MajorStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface MajorRepository
        extends JpaRepository<Major, UUID>,
        JpaSpecificationExecutor<Major> {


    Optional<Major>
    findByIdAndDeletedAtIsNull(
            UUID id
    );


    Optional<Major>
    findByUniversity_IdAndCodeIgnoreCaseAndDeletedAtIsNull(
            UUID universityId,
            String code
    );


    boolean
    existsByUniversity_IdAndCodeIgnoreCaseAndDeletedAtIsNull(
            UUID universityId,
            String code
    );


    boolean
    existsByUniversity_IdAndCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(
            UUID universityId,
            String code,
            UUID id
    );


    List<Major>
    findAllByUniversity_IdAndStatusAndDeletedAtIsNullOrderByNameAsc(
            UUID universityId,
            MajorStatus status
    );


    List<Major>
    findAllByFaculty_IdAndStatusAndDeletedAtIsNullOrderByNameAsc(
            UUID facultyId,
            MajorStatus status
    );


    List<Major>
    findAllByStatusAndDeletedAtIsNullOrderByNameAsc(
            MajorStatus status
    );
}   