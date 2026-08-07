package com.hubt.assistant.security.principal;

import com.hubt.assistant.identity.user.entity.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CurrentUser implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String fullName;
    private final String universityCode;
    private final AccountStatus accountStatus;
    private final List<String> roles;
    private final List<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public CurrentUser(
            UUID id,
            String email,
            String password,
            String fullName,
            String universityCode,
            AccountStatus accountStatus,
            List<String> roles,
            List<String> permissions,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.universityCode = universityCode;
        this.accountStatus = accountStatus;
        this.roles = roles;
        this.permissions = permissions;
        this.authorities = authorities;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUniversityCode() {
        return universityCode;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}