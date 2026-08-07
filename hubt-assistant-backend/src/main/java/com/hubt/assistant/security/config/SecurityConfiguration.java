package com.hubt.assistant.security.config;

import com.hubt.assistant.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================
                .csrf(csrf -> csrf.disable())

                // =================================================
                // SESSION
                // =================================================
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================
                .authorizeHttpRequests(
                        auth -> auth

                                // =================================
                                // PUBLIC API
                                // =================================
                                .requestMatchers(
                                        "/api/v1/public/**",

                                        "/api/v1/auth/register",
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/refresh-token",
                                        "/api/v1/auth/forgot-password",
                                        "/api/v1/auth/verify-reset-otp",
                                        "/api/v1/auth/reset-password",

                                        "/uploads/**",

                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",

                                        "/actuator/health"
                                )
                                .permitAll()

                                // =================================
                                // AUTHENTICATED AUTH API
                                // =================================
                                .requestMatchers(
                                        "/api/v1/auth/me",
                                        "/api/v1/auth/change-password",
                                        "/api/v1/auth/logout"
                                )
                                .authenticated()

                                // =================================
                                // CANDIDATE API
                                // =================================
                                .requestMatchers(
                                        "/api/v1/candidates/**"
                                )
                                .authenticated()

                                // =================================
                                // DEFAULT
                                // =================================
                                .anyRequest()
                                .authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }
}