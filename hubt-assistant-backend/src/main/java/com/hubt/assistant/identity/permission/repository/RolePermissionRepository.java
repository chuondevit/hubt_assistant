package com.hubt.assistant.identity.permission.repository;

import com.hubt.assistant.identity.permission.entity.RolePermission;
import com.hubt.assistant.identity.permission.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findAllByRoleId(UUID roleId);

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId
    );
}