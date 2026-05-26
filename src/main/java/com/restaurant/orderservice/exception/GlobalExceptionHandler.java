package com.restaurant.orderservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception handling for all controllers.
 *
 * @RestControllerAdvice intercepts exceptions thrown anywhere in the controller
 * layer and maps them to structured HTTP responses — so individual controllers
 * don't need try/catch blocks. This is the standard Spring Boot approach.
 *
 * Handler priority: Spring matches the most specific handler first.
 * The generic Exception handler is a safety net for anything unhandled.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 404 Not Found ───────────────────────────────────────────────────────

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
            OrderNotFoundException ex, HttpServletRequest request) {

        log.error("OrderNotFoundException: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─── 400 Validation Error ─────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("Validation error on {}: {}", request.getRequestURI(), ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String fieldName = ((FieldError) objectError).getField();
            String errorMessage = objectError.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Request validation failed. See 'fieldErrors' for details.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ─── 400 Invalid JSON / Wrong Enum Value ─────────────────────────────────
    // Triggered when the request body has wrong enum values like "Completed" or "Success"

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJsonException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.error("Invalid request body on {}: {}", request.getRequestURI(), ex.getMessage());

        String message = "Invalid value in request body.";

        // Give a helpful hint if an enum value is wrong
        String cause = ex.getMessage();
        if (cause != null && cause.contains("not one of the values accepted")) {
            message = buildEnumHint(cause);
        } else if (cause != null && cause.contains("Cannot deserialize value of type")) {
            message = "Invalid value provided. Check that enum fields use the correct allowed values.";
        }

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_REQUEST_BODY")
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private String buildEnumHint(String cause) {
        // Extract the list of allowed values from the Jackson error message and display them
        try {
            int start = cause.indexOf("[");
            int end = cause.indexOf("]");
            if (start != -1 && end != -1) {
                String allowed = cause.substring(start, end + 1);
                return "Invalid status value. Allowed values are: " + allowed;
            }
        } catch (Exception ignored) {}
        return "Invalid status value. Please use one of the allowed enum values.";
    }

    // ─── 500 Internal Server Error (catch-all) ────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}