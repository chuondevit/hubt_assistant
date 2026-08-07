package com.hubt.assistant.security.principal;

import com.hubt.assistant.identity.permission.repository.PermissionRepository;
import com.hubt.assistant.identity.role.entity.UserRole;
import com.hubt.assistant.identity.role.repository.UserRoleRepository;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCaseAndDeletedAtIsNull(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng"
                ));

        List<UserRole> userRoles =
                userRoleRepository.findActiveRolesByUserId(user.getId());

        List<String> roles = userRoles.stream()
                .map(userRole -> userRole.getRole().getCode())
                .distinct()
                .sorted()
                .toList();

        List<String> permissions =
                permissionRepository.findPermissionCodesByUserId(
                        user.getId()
                );

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        roles.forEach(role ->
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role)
                )
        );

        permissions.forEach(permission ->
                authorities.add(
                        new SimpleGrantedAuthority(permission)
                )
        );

        String universityCode = user.getUniversity() == null
                ? null
                : user.getUniversity().getCode();

        return new CurrentUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                universityCode,
                user.getAccountStatus(),
                roles,
                permissions,
                authorities
        );
    }
}