package com.hubt.assistant.admission.combination.repository;

import com.hubt.assistant.admission.combination.entity.SubjectCombinationItem;
import com.hubt.assistant.admission.combination.entity.SubjectCombinationItemId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectCombinationItemRepository
        extends JpaRepository<
                SubjectCombinationItem,
                SubjectCombinationItemId
        > {
}