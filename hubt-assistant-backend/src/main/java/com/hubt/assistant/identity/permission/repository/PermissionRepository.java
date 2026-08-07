package com.hubt.assistant.identity.permission.repository;

import com.hubt.assistant.identity.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(String code);

    @Query("""
            SELECT DISTINCT p.code
            FROM UserRole ur
            JOIN RolePermission rp
                ON rp.role.id = ur.role.id
            JOIN Permission p
                ON p.id = rp.permission.id
            WHERE ur.user.id = :userId
              AND ur.active = true
              AND ur.role.active = true
              AND (
                    ur.expiredAt IS NULL
                    OR ur.expiredAt > CURRENT_TIMESTAMP
              )
            ORDER BY p.code
            """)
    List<String> findPermissionCodesByUserId(
            @Param("userId") UUID userId
    );
}