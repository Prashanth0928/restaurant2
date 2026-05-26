package com.restaurant.orderservice.controller;

import com.restaurant.orderservice.dto.ApiResponse;
import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.service.OrderService;
import com.restaurant.orderservice.service.OrderWebClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing the Order Management API.
 *
 * Responsibilities: receive HTTP requests, delegate to the service layer,
 * and return structured HTTP responses. Business logic lives in the service.
 *
 * @RestController = @Controller + @ResponseBody (auto-serialises returns to JSON)
 * @RequestMapping("/orders") sets the base path for all endpoints in this class.
 *
 * Swagger annotations (@Operation, @ApiResponses, @Tag) appear in Swagger UI at:
 * http://localhost:8080/swagger-ui.html
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "CRUD operations for restaurant orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderWebClientService orderWebClientService;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /orders — Create a new order
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Accepts order details and creates a new restaurant order. Returns 201 with the created order."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation failed — check the fieldErrors in the response"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderRequestDTO requestDTO) {

        log.info("POST /orders — customer: {}", requestDTO.getCustomerName());
        OrderResponseDTO response = orderService.createOrder(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /orders/{orderId} — Fetch a single order
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves full details of a specific order using its unique ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No order exists with the given ID"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(
            @Parameter(description = "Unique order ID", example = "ORD-A1B2C3D4")
            @PathVariable String orderId) {

        log.info("GET /orders/{}", orderId);
        OrderResponseDTO response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order fetched successfully", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /orders/{orderId} — Update an existing order
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{orderId}")
    @Operation(
            summary = "Update an existing order",
            description = "Replaces all updatable fields of the order. orderId and orderTime are immutable."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrder(
            @Parameter(description = "Unique order ID", example = "ORD-A1B2C3D4")
            @PathVariable String orderId,
            @Valid @RequestBody OrderRequestDTO requestDTO) {

        log.info("PUT /orders/{}", orderId);
        OrderResponseDTO response = orderService.updateOrder(orderId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Order updated successfully", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /orders/{orderId} — Remove an order
    // ─────────────────────────────────────────────────────────────────────────

    @DeleteMapping("/{orderId}")
    @Operation(
            summary = "Delete an order",
            description = "Permanently removes the order with the given ID. Returns 404 if it does not exist."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "Unique order ID", example = "ORD-A1B2C3D4")
            @PathVariable String orderId) {

        log.info("DELETE /orders/{}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /orders — List all orders
    // ─────────────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────────────
    // GET /orders/webclient/{orderId} — Fetch order using WebClient
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/webclient/{orderId}")
    @Operation(
            summary = "Get order using WebClient",
            description = "Fetches order details by calling our own GET /orders/{orderId} endpoint " +
                    "internally via Spring WebClient. This demonstrates how WebClient works — " +
                    "in real projects you would call an external API (payment gateway, maps, etc.) the same way."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order fetched successfully via WebClient"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderViaWebClient(
            @Parameter(description = "Unique order ID", example = "ORD-A1B2C3D4")
            @PathVariable String orderId) {

        log.info("GET /orders/webclient/{} — using WebClient", orderId);
        OrderResponseDTO response = orderWebClientService.fetchOrderViaWebClient(orderId);
        return ResponseEntity.ok(ApiResponse.success(
                "Order fetched successfully via WebClient", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /orders — List all orders
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(
            summary = "Get all orders",
            description = "Returns a list of all orders currently in the system."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Orders fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        log.info("GET /orders — fetching all");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orders));
    }
}