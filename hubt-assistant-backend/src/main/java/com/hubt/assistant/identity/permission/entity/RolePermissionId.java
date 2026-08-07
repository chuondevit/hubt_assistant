package com.hubt.assistant.identity.permission.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {

    private UUID role;
    private UUID permission;
}