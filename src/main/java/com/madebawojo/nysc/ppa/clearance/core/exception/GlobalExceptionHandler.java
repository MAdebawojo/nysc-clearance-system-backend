package com.madebawojo.nysc.ppa.clearance.core.exception;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.net.BindException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleApiException(ApiException ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponseStructure.error(ex.getMessage(), ex.getStatus().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseStructure<?>> handleGenericException(Exception ex) {
        log.warn("Internal Server error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseStructure.error("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Method Argument violation: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(ApiResponseStructure.error("Validation Failed", errors, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseStructure.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unathorized: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseStructure.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.warn("Attempt to register existing user: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseStructure.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP Method not support for this endpoint: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponseStructure.error("HTTP method not allowed for this endpoint", HttpStatus.METHOD_NOT_ALLOWED.value()));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseStructure<?>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseStructure.error("Validation Failed", errors, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseStructure<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseStructure.error("Invalid parameter type", List.of(ex.getMessage()), HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiResponseStructure<Object>> handleAllAccessDeniedExceptions(RuntimeException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseStructure.error("Access Denied: You do not have permission to perform this action", HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseStructure<Object>> handleBindException(BindException ex) {
        log.warn("Bind Exception occurred: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseStructure.error("Invalid request: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseStructure<Object>> handleBindException(HttpMessageNotReadableException ex) {
        log.warn("HttpMessageNotReadbleException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseStructure.error("Malformed or missing request body. Please check your JSON syntax.", HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiResponseStructure<String>> handleBusinessConflict(BusinessConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseStructure.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }
}

