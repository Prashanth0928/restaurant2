package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.ApiResponse;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Demonstrates WebClient by calling our own REST API over HTTP.
 *
 * Real-world use: replace the URI with any external API
 * (payment gateway, delivery tracker, maps service, etc.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderWebClientService {

    private final WebClient webClient;

    /**
     * Uses WebClient to call GET /orders/{orderId} on our own running server.
     * This is exactly how you would call any external REST API.
     */
    public OrderResponseDTO fetchOrderViaWebClient(String orderId) {
        log.info("WebClient → calling GET /orders/{}", orderId);

        ApiResponse<OrderResponseDTO> response = webClient
                .get()
                .uri("/orders/{orderId}", orderId)
                .retrieve()
                // If the other API returns 404, throw our custom exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("WebClient received 4xx for orderId: {}", orderId);
                    return Mono.error(new OrderNotFoundException(
                            "Order not found via WebClient for ID: " + orderId));
                })
                // If the other API returns 500, throw a runtime error
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("WebClient received 5xx for orderId: {}", orderId);
                    return Mono.error(new RuntimeException(
                            "External server error while fetching order: " + orderId));
                })
                // Read the response body into ApiResponse<OrderResponseDTO>
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<OrderResponseDTO>>() {})
                .block(); // .block() waits for the response (synchronous style)

        log.info("WebClient ← received response for orderId: {}", orderId);

        return response != null ? response.getData() : null;
    }
}