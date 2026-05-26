package com.restaurant.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper used by all endpoints.
 *
 * Wrapping every response in a consistent envelope means clients always know
 * where to find the data, status, and message — regardless of which endpoint
 * they call. This is a common industry pattern for REST APIs.
 *
 * @JsonInclude(NON_NULL) prevents null fields (like 'data' on error responses)
 * from appearing in the JSON output, keeping responses clean.
 *
 * @param <T> the type of the payload — e.g., OrderResponseDTO or List<OrderResponseDTO>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response envelope used across all endpoints")
public class ApiResponse<T> {

    @Schema(description = "Outcome of the request", example = "success", allowableValues = {"success", "error"})
    private String status;

    @Schema(description = "Human-readable description of the result", example = "Order created successfully")
    private String message;

    @Schema(description = "Response payload — present on success, absent on error")
    private T data;

    @Schema(description = "Server timestamp when this response was generated")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}