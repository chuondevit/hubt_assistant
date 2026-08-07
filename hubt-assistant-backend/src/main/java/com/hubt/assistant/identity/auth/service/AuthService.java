package com.hubt.assistant.identity.auth.service;

import com.hubt.assistant.common.exception.BusinessException;
import com.hubt.assistant.common.exception.ConflictException;
import com.hubt.assistant.common.exception.ResourceNotFoundException;
import com.hubt.assistant.identity.auth.dto.request.ChangePasswordRequest;
import com.hubt.assistant.identity.auth.dto.request.LoginRequest;
import com.hubt.assistant.identity.auth.dto.request.LogoutRequest;
import com.hubt.assistant.identity.auth.dto.request.RefreshTokenRequest;
import com.hubt.assistant.identity.auth.dto.request.RegisterRequest;
import com.hubt.assistant.identity.auth.dto.response.AuthResponse;
import com.hubt.assistant.identity.auth.dto.response.RegisterResponse;
import com.hubt.assistant.identity.auth.entity.RefreshToken;
import com.hubt.assistant.identity.profile.entity.CandidateProfile;
import com.hubt.assistant.identity.profile.repository.CandidateProfileRepository;
import com.hubt.assistant.identity.role.entity.Role;
import com.hubt.assistant.identity.role.entity.UserRole;
import com.hubt.assistant.identity.role.repository.RoleRepository;
import com.hubt.assistant.identity.role.repository.UserRoleRepository;
import com.hubt.assistant.identity.user.entity.AccountStatus;
import com.hubt.assistant.identity.user.entity.User;
import com.hubt.assistant.identity.user.repository.UserRepository;
import com.hubt.assistant.organization.university.entity.University;
import com.hubt.assistant.organization.university.repository.UniversityRepository;
import com.hubt.assistant.security.jwt.JwtProperties;
import com.hubt.assistant.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UniversityRepository universityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "EMAIL_ALREADY_EXISTS",
                    "Email đã được sử dụng"
            );
        }

        University university = universityRepository
                .findByCodeAndDeletedAtIsNull("HUBT")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UNIVERSITY_NOT_FOUND",
                        "Không tìm thấy dữ liệu trường HUBT"
                ));

        Role candidateRole = roleRepository
                .findByCodeAndActiveTrue("CANDIDATE")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ROLE_NOT_FOUND",
                        "Không tìm thấy vai trò CANDIDATE"
                ));

        User user = new User();
        user.setUniversity(university);
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        User savedUser = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(candidateRole);
        userRole.setUniversity(university);
        userRole.setAssignedAt(Instant.now());
        userRole.setActive(true);

        userRoleRepository.save(userRole);

        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setUser(savedUser);

        candidateProfileRepository.save(candidateProfile);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                candidateRole.getCode(),
                savedUser.getAccountStatus().name()
        );
    }

    @Transactional
    public AuthResponse login(
            LoginRequest request,
            String ipAddress,
            String userAgent
    ) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BadCredentialsException(
                        "Email hoặc mật khẩu không đúng"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                    "Email hoặc mật khẩu không đúng"
            );
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ConflictException(
                    "ACCOUNT_NOT_ACTIVE",
                    "Tài khoản hiện không hoạt động"
            );
        }

        List<String> roles = getActiveRoleCodes(user.getId());

        String accessToken =
                jwtTokenProvider.generateAccessToken(
                        user.getId(),
                        user.getEmail(),
                        roles
                );

        String refreshToken =
                refreshTokenService.create(
                        user,
                        ipAddress,
                        userAgent
                );

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                roles,
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getAccessTokenExpiration() / 1000
        );
    }

    @Transactional
    public AuthResponse refreshToken(
            RefreshTokenRequest request,
            String ipAddress,
            String userAgent
    ) {

        RefreshToken storedToken =
                refreshTokenService.validate(
                        request.refreshToken()
                );

        User user = storedToken.getUser();

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ConflictException(
                    "ACCOUNT_NOT_ACTIVE",
                    "Tài khoản hiện không hoạt động"
            );
        }

        List<String> roles = getActiveRoleCodes(user.getId());

        String accessToken =
                jwtTokenProvider.generateAccessToken(
                        user.getId(),
                        user.getEmail(),
                        roles
                );

        /*
         * Refresh token rotation:
         * thu hồi token cũ rồi tạo token mới.
         */
        refreshTokenService.revoke(
                request.refreshToken()
        );

        String newRefreshToken =
                refreshTokenService.create(
                        user,
                        ipAddress,
                        userAgent
                );

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                roles,
                accessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.getAccessTokenExpiration() / 1000
        );
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(
                request.refreshToken()
        );
    }

    @Transactional
    public void changePassword(
            UUID userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_NOT_FOUND",
                        "Không tìm thấy người dùng"
                ));

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessException(
                    "CURRENT_PASSWORD_INCORRECT",
                    "Mật khẩu hiện tại không đúng"
            );
        }

        if (!request.newPassword()
                .equals(request.confirmPassword())) {
            throw new BusinessException(
                    "PASSWORD_CONFIRMATION_MISMATCH",
                    "Xác nhận mật khẩu không khớp"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessException(
                    "PASSWORD_NOT_CHANGED",
                    "Mật khẩu mới phải khác mật khẩu hiện tại"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(user);

        /*
         * Sau khi đổi mật khẩu, thu hồi toàn bộ refresh token
         * để các phiên đăng nhập cũ không thể làm mới access token.
         */
        refreshTokenService.revokeAllByUserId(userId);
    }

    private List<String> getActiveRoleCodes(UUID userId) {
        return userRoleRepository
                .findActiveRolesByUserId(userId)
                .stream()
                .map(userRole -> userRole.getRole().getCode())
                .distinct()
                .sorted()
                .toList();
    }
}