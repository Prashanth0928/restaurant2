# Restaurant Order Management Service

A production-ready REST API built with **Java 17**, **Spring Boot 3**, and **Spring WebClient**. Manage restaurant orders end-to-end with full CRUD support, input validation, structured error handling, and interactive Swagger UI documentation.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Sample Requests & Responses](#sample-requests--responses)
- [Postman Examples (curl)](#postman-examples-curl)
- [Order & Payment Statuses](#order--payment-statuses)
- [Error Handling](#error-handling)
- [Architecture Overview](#architecture-overview)
- [WebClient Usage](#webclient-usage)

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language (LTS) |
| Spring Boot | 3.2.5 | Application framework |
| Spring Web | 6.x | REST API (MVC, Tomcat) |
| Spring WebFlux | 6.x | WebClient for reactive HTTP calls |
| SpringDoc OpenAPI | 2.3.0 | Swagger UI + OpenAPI 3 spec |
| Lombok | Latest | Boilerplate reduction |
| Bean Validation | Jakarta 3 | Request DTO validation |
| SLF4J + Logback | Built-in | Structured application logging |
| Maven | 3.x | Build and dependency management |

---

## Project Structure

```
restaurant-order-service/
├── pom.xml                                        # Maven dependencies and build config
├── README.md
└── src/
    └── main/
        ├── java/com/restaurant/orderservice/
        │   ├── RestaurantOrderServiceApplication.java   # Entry point (@SpringBootApplication)
        │   │
        │   ├── config/
        │   │   ├── SwaggerConfig.java                  # OpenAPI metadata configuration
        │   │   └── WebClientConfig.java                # WebClient bean with timeouts + logging
        │   │
        │   ├── controller/
        │   │   └── OrderController.java                # REST endpoints (HTTP in/out)
        │   │
        │   ├── service/
        │   │   ├── OrderService.java                   # Business logic interface
        │   │   └── impl/
        │   │       └── OrderServiceImpl.java           # Business logic implementation
        │   │
        │   ├── repository/
        │   │   ├── OrderRepository.java                # Data access interface
        │   │   └── impl/
        │   │       └── OrderRepositoryImpl.java        # In-memory ConcurrentHashMap store
        │   │
        │   ├── model/
        │   │   ├── Order.java                          # Core domain entity
        │   │   ├── OrderStatus.java                    # Enum: PENDING → DELIVERED
        │   │   └── PaymentStatus.java                  # Enum: PENDING → PAID / REFUNDED
        │   │
        │   ├── dto/
        │   │   ├── OrderRequestDTO.java                # Validated input for POST / PUT
        │   │   ├── OrderResponseDTO.java               # Output shape for all responses
        │   │   └── ApiResponse.java                    # Generic response envelope { status, message, data }
        │   │
        │   └── exception/
        │       ├── OrderNotFoundException.java         # Thrown when orderId doesn't exist (→ 404)
        │       ├── ErrorResponse.java                  # Structured error payload shape
        │       └── GlobalExceptionHandler.java         # @RestControllerAdvice — maps exceptions to HTTP
        │
        └── resources/
            └── application.yml                         # Server port, logging, Swagger, WebClient config
```

---

## Prerequisites

| Requirement | Minimum Version |
|---|---|
| Java JDK | 17+ |
| Apache Maven | 3.6+ |
| Git | Any |

Verify your setup:
```bash
java -version    # Should print: openjdk 17...
mvn -version     # Should print: Apache Maven 3.x...
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd restaurant-order-service
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Or run the JAR directly after building:

```bash
java -jar target/restaurant-order-service-1.0.0.jar
```

### 4. Verify it's running

```
http://localhost:8080/swagger-ui.html   ← Interactive API docs (recommended)
http://localhost:8080/api-docs           ← Raw OpenAPI 3 JSON spec
http://localhost:8080/orders             ← Returns [] when no orders exist yet
```

---

## API Endpoints

| Method | Endpoint | Description | HTTP Status |
|---|---|---|---|
| `POST` | `/orders` | Create a new order | `201 Created` |
| `GET` | `/orders/{orderId}` | Fetch a single order by ID | `200 OK` |
| `PUT` | `/orders/{orderId}` | Update all fields of an existing order | `200 OK` |
| `DELETE` | `/orders/{orderId}` | Delete an order | `200 OK` |
| `GET` | `/orders` | List all orders | `200 OK` |

All responses use the `ApiResponse<T>` envelope:

```json
{
  "status": "success",
  "message": "Order created successfully",
  "data": { ... },
  "timestamp": "2024-05-22T10:30:00"
}
```

---

## Sample Requests & Responses

### Create Order — `POST /orders`

**Request Body:**
```json
{
  "customerName": "John Doe",
  "orderedDish": "Margherita Pizza",
  "quantity": 2,
  "orderPrice": 25.99,
  "orderStatus": "PENDING",
  "restaurantName": "Mario's Italian Kitchen",
  "deliveryAddress": "123 Main Street, Springfield, IL 62701",
  "paymentStatus": "PENDING"
}
```

**Response — `201 Created`:**
```json
{
  "status": "success",
  "message": "Order created successfully",
  "data": {
    "orderId": "ORD-A1B2C3D4",
    "customerName": "John Doe",
    "orderedDish": "Margherita Pizza",
    "quantity": 2,
    "orderPrice": 25.99,
    "orderStatus": "PENDING",
    "restaurantName": "Mario's Italian Kitchen",
    "orderTime": "2024-05-22T10:30:00",
    "deliveryAddress": "123 Main Street, Springfield, IL 62701",
    "paymentStatus": "PENDING"
  },
  "timestamp": "2024-05-22T10:30:00"
}
```

---

### Get Order — `GET /orders/{orderId}`

**Response — `200 OK`:**
```json
{
  "status": "success",
  "message": "Order fetched successfully",
  "data": {
    "orderId": "ORD-A1B2C3D4",
    "customerName": "John Doe",
    "orderedDish": "Margherita Pizza",
    "quantity": 2,
    "orderPrice": 25.99,
    "orderStatus": "CONFIRMED",
    "restaurantName": "Mario's Italian Kitchen",
    "orderTime": "2024-05-22T10:30:00",
    "deliveryAddress": "123 Main Street, Springfield, IL 62701",
    "paymentStatus": "PAID"
  },
  "timestamp": "2024-05-22T10:35:00"
}
```

---

### Update Order — `PUT /orders/{orderId}`

**Request Body:**
```json
{
  "customerName": "John Doe",
  "orderedDish": "Pepperoni Pizza",
  "quantity": 3,
  "orderPrice": 35.99,
  "orderStatus": "CONFIRMED",
  "restaurantName": "Mario's Italian Kitchen",
  "deliveryAddress": "123 Main Street, Springfield, IL 62701",
  "paymentStatus": "PAID"
}
```

**Response — `200 OK`:**
```json
{
  "status": "success",
  "message": "Order updated successfully",
  "data": {
    "orderId": "ORD-A1B2C3D4",
    "customerName": "John Doe",
    "orderedDish": "Pepperoni Pizza",
    "quantity": 3,
    "orderPrice": 35.99,
    "orderStatus": "CONFIRMED",
    "restaurantName": "Mario's Italian Kitchen",
    "orderTime": "2024-05-22T10:30:00",
    "deliveryAddress": "123 Main Street, Springfield, IL 62701",
    "paymentStatus": "PAID"
  },
  "timestamp": "2024-05-22T10:40:00"
}
```

---

### Delete Order — `DELETE /orders/{orderId}`

**Response — `200 OK`:**
```json
{
  "status": "success",
  "message": "Order deleted successfully",
  "timestamp": "2024-05-22T10:45:00"
}
```

---

### Not Found — `GET /orders/ORD-INVALID`

**Response — `404 Not Found`:**
```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Order not found with ID: ORD-INVALID",
  "path": "/orders/ORD-INVALID",
  "timestamp": "2024-05-22T10:30:00"
}
```

---

### Validation Error — `POST /orders` (missing fields)

**Response — `400 Bad Request`:**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed. See 'fieldErrors' for details.",
  "path": "/orders",
  "timestamp": "2024-05-22T10:30:00",
  "fieldErrors": {
    "customerName": "Customer name is required",
    "orderPrice": "Order price must be greater than 0"
  }
}
```

---

## Postman Examples (curl)

**Create order:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Smith",
    "orderedDish": "Chicken Tikka Masala",
    "quantity": 1,
    "orderPrice": 18.50,
    "restaurantName": "Spice Garden",
    "deliveryAddress": "456 Oak Ave, Chicago, IL 60601"
  }'
```

**Get order:**
```bash
curl http://localhost:8080/orders/ORD-A1B2C3D4
```

**Update order:**
```bash
curl -X PUT http://localhost:8080/orders/ORD-A1B2C3D4 \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Smith",
    "orderedDish": "Chicken Tikka Masala",
    "quantity": 2,
    "orderPrice": 37.00,
    "orderStatus": "PREPARING",
    "restaurantName": "Spice Garden",
    "deliveryAddress": "456 Oak Ave, Chicago, IL 60601",
    "paymentStatus": "PAID"
  }'
```

**Delete order:**
```bash
curl -X DELETE http://localhost:8080/orders/ORD-A1B2C3D4
```

**List all orders:**
```bash
curl http://localhost:8080/orders
```

---

## Order & Payment Statuses

### OrderStatus lifecycle

```
PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
                                                    ↘
                                                   CANCELLED
```

| Value | Meaning |
|---|---|
| `PENDING` | Order placed, awaiting restaurant confirmation |
| `CONFIRMED` | Restaurant accepted the order |
| `PREPARING` | Kitchen is preparing the food |
| `OUT_FOR_DELIVERY` | Delivery rider has picked up the order |
| `DELIVERED` | Order handed to the customer |
| `CANCELLED` | Order was cancelled by customer or restaurant |

### PaymentStatus values

| Value | Meaning |
|---|---|
| `PENDING` | Payment not yet processed |
| `PAID` | Payment completed successfully |
| `FAILED` | Payment attempt failed |
| `REFUNDED` | Amount returned to customer (e.g., after cancellation) |

---

## Error Handling

All errors return a structured `ErrorResponse` body:

| HTTP Code | Triggered By | `error` field |
|---|---|---|
| `400` | Invalid/missing request fields | `VALIDATION_ERROR` |
| `404` | Order ID not found | `NOT_FOUND` |
| `500` | Unhandled internal errors | `INTERNAL_SERVER_ERROR` |

Validation errors additionally include a `fieldErrors` map showing which fields failed and why.

---

## Architecture Overview

```
HTTP Request
     │
     ▼
┌─────────────────────┐
│   OrderController   │  ← Validates input (@Valid), delegates to service
└─────────────────────┘
     │
     ▼
┌─────────────────────┐
│   OrderService      │  ← Business logic (create ID, set defaults, timestamps)
└─────────────────────┘
     │
     ▼
┌─────────────────────┐
│  OrderRepository    │  ← Data access (ConcurrentHashMap — swap for JPA easily)
└─────────────────────┘
     │
     ▼
  In-Memory Store
```

**Layer responsibilities:**

- **Controller** — HTTP protocol concerns (status codes, request parsing, response shaping). No business logic.
- **Service** — Business rules (ID generation, default status values, immutable field preservation).
- **Repository** — Data access. The interface decouples storage technology from business logic.
- **DTOs** — `OrderRequestDTO` validates input; `OrderResponseDTO` controls output shape. Neither leaks internals.
- **GlobalExceptionHandler** — One place to map all exceptions to HTTP responses. Controllers stay clean.

---

## WebClient Usage

`WebClientConfig` creates a pre-configured `WebClient` bean with:
- Connection timeout: 5 seconds
- Read timeout: 5 seconds
- Default JSON headers
- Request/response logging filter

**Inject and use in any service:**

```java
@Service
@RequiredArgsConstructor
public class ExternalOrderService {

    private final WebClient webClient;

    public OrderResponseDTO fetchFromExternalSystem(String orderId) {
        return webClient.get()
                .uri("/external/orders/{id}", orderId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                          resp -> Mono.error(new OrderNotFoundException("External order not found")))
                .bodyToMono(OrderResponseDTO.class)
                .block();  // Use .subscribe() for fully non-blocking
    }
}
```

The base URL is configurable via `webclient.base-url` in `application.yml`.
