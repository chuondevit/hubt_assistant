package com.hubt.assistant.identity.role.repository;

import com.hubt.assistant.identity.role.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UUID> {

    List<UserRole> findAllByUserIdAndActiveTrue(UUID userId);

    @Query("""
            SELECT ur
            FROM UserRole ur
            JOIN FETCH ur.role r
            WHERE ur.user.id = :userId
              AND ur.active = true
              AND (
                    ur.expiredAt IS NULL
                    OR ur.expiredAt > CURRENT_TIMESTAMP
              )
            """)
    List<UserRole> findActiveRolesByUserId(
            @Param("userId") UUID userId
    );
}