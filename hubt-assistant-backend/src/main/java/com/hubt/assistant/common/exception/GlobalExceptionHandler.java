package com.hubt.assistant.common.exception;

import com.hubt.assistant.common.api.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiErrorResponse(
                                false,
                                ex.getCode(),
                                ex.getMessage(),
                                Map.of(),
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiErrorResponse(
                                false,
                                ex.getCode(),
                                ex.getMessage(),
                                Map.of(),
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiErrorResponse(
                                false,
                                "INVALID_CREDENTIALS",
                                "Email hoặc mật khẩu không đúng",
                                Map.of(),
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity.badRequest()
                .body(
                        new ApiErrorResponse(
                                false,
                                "VALIDATION_ERROR",
                                "Dữ liệu không hợp lệ",
                                errors,
                                Instant.now()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex
    ) {
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiErrorResponse(
                                false,
                                "INTERNAL_SERVER_ERROR",
                                ex.getMessage(),
                                Map.of(),
                                Instant.now()
                        )
                );
    }

}