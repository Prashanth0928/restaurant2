package com.restaurant.orderservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Structured error payload returned by GlobalExceptionHandler on failure.
 *
 * Having a consistent error shape means clients always know what fields
 * to check — reducing integration friction compared to plain string messages.
 *
 * @JsonInclude(NON_NULL) hides 'fieldErrors' when it's null (i.e., non-validation errors).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error details returned when a request fails")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Short error category", example = "NOT_FOUND")
    private String error;

    @Schema(description = "Detailed error message", example = "Order not found with ID: ORD-XYZ")
    private String message;

    @Schema(description = "The request URI that triggered this error", example = "/orders/ORD-XYZ")
    private String path;

    @Schema(description = "When this error occurred")
    private LocalDateTime timestamp;

    @Schema(description = "Per-field validation errors — only present on 400 validation failures")
    private Map<String, String> fieldErrors;
}