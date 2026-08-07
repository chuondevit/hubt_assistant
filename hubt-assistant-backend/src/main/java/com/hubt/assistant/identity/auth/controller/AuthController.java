package com.hubt.assistant.identity.auth.controller;

import com.hubt.assistant.common.api.ApiResponse;

import com.hubt.assistant.identity.auth.dto.request.ChangePasswordRequest;
import com.hubt.assistant.identity.auth.dto.request.ForgotPasswordRequest;
import com.hubt.assistant.identity.auth.dto.request.LoginRequest;
import com.hubt.assistant.identity.auth.dto.request.LogoutRequest;
import com.hubt.assistant.identity.auth.dto.request.RefreshTokenRequest;
import com.hubt.assistant.identity.auth.dto.request.RegisterRequest;
import com.hubt.assistant.identity.auth.dto.request.ResetPasswordRequest;
import com.hubt.assistant.identity.auth.dto.request.VerifyResetOtpRequest;

import com.hubt.assistant.identity.auth.dto.response.AuthResponse;
import com.hubt.assistant.identity.auth.dto.response.CurrentUserResponse;
import com.hubt.assistant.identity.auth.dto.response.RegisterResponse;
import com.hubt.assistant.identity.auth.dto.response.VerifyResetOtpResponse;

import com.hubt.assistant.identity.auth.service.AuthService;
import com.hubt.assistant.identity.auth.service.PasswordResetService;

import com.hubt.assistant.security.principal.CurrentUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final PasswordResetService passwordResetService;


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(

            @Valid
            @RequestBody
            RegisterRequest request

    ) {

        RegisterResponse result =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Đăng ký tài khoản thành công",
                                result
                        )
                );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(

            @Valid
            @RequestBody
            LoginRequest request,

            HttpServletRequest httpRequest

    ) {

        AuthResponse result =
                authService.login(
                        request,
                        getClientIp(httpRequest),
                        httpRequest.getHeader(
                                "User-Agent"
                        )
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Đăng nhập thành công",
                        result
                )
        );
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(

            @Valid
            @RequestBody
            RefreshTokenRequest request,

            HttpServletRequest httpRequest

    ) {

        AuthResponse result =
                authService.refreshToken(
                        request,
                        getClientIp(httpRequest),
                        httpRequest.getHeader(
                                "User-Agent"
                        )
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Làm mới token thành công",
                        result
                )
        );
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(

            @Valid
            @RequestBody
            LogoutRequest request

    ) {

        authService.logout(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Đăng xuất thành công"
                )
        );
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(

            @AuthenticationPrincipal
            CurrentUser currentUser,

            @Valid
            @RequestBody
            ChangePasswordRequest request

    ) {

        authService.changePassword(
                currentUser.getId(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Đổi mật khẩu thành công. Vui lòng đăng nhập lại"
                )
        );
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> me(

            @AuthenticationPrincipal
            CurrentUser currentUser

    ) {

        CurrentUserResponse response =
                new CurrentUserResponse(

                        currentUser.getId(),

                        currentUser.getFullName(),

                        currentUser.getUsername(),

                        currentUser.getUniversityCode(),

                        currentUser
                                .getAccountStatus()
                                .name(),

                        currentUser.getRoles(),

                        currentUser.getPermissions()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin người dùng thành công",
                        response
                )
        );
    }


    // =========================================================
    // FORGOT PASSWORD
    // Gửi OTP 6 số qua email
    // =========================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(

            @Valid
            @RequestBody
            ForgotPasswordRequest request,

            HttpServletRequest httpRequest

    ) {

        passwordResetService.requestReset(

                request.email(),

                getClientIp(httpRequest)
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Nếu email tồn tại, mã OTP sẽ được gửi tới email"
                )
        );
    }


    // =========================================================
    // VERIFY RESET OTP
    // =========================================================

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<VerifyResetOtpResponse>>
    verifyResetOtp(

            @Valid
            @RequestBody
            VerifyResetOtpRequest request

    ) {

        VerifyResetOtpResponse result =
                passwordResetService.verifyOtp(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xác thực OTP thành công",
                        result
                )
        );
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(

            @Valid
            @RequestBody
            ResetPasswordRequest request

    ) {

        passwordResetService
                .resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Đặt lại mật khẩu thành công"
                )
        );
    }


    // =========================================================
    // CLIENT IP
    // =========================================================

    private String getClientIp(
            HttpServletRequest request
    ) {

        String forwardedFor =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }

}