# Restaurant Order Management Service

A full-stack restaurant order management app built with **Spring Boot 3**, **React 18**, and **PostgreSQL**.

**Live demo:** https://restaurant2-64pr.onrender.com

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.5, Spring Data JPA |
| Database | H2 (local dev), PostgreSQL via Neon (production) |
| HTTP Client | Spring WebClient (internal + TheMealDB external API) |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Frontend | React 18, Vite |
| Deployment | Render (Docker), Neon (PostgreSQL) |

---

## Features

- Full CRUD for restaurant orders (Create, Read, Update, Delete)
- Order status lifecycle: `PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED`
- Payment status tracking: `PENDING / PAID / FAILED / REFUNDED`
- Meal search powered by TheMealDB external API
- React frontend bundled inside the Spring Boot JAR — single deployable unit
- Swagger UI for API exploration

---

## Run Locally

### Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17+ |
| Maven | 3.6+ |

> Node.js is **not required** — Maven downloads it automatically during the build.

Verify:
```bash
java -version   # should print openjdk 17...
mvn -version    # should print Apache Maven 3.x...
```

### Steps

```bash
# 1. Clone
git clone https://github.com/Prashanth0928/restaurant2.git
cd restaurant2

# 2. Run
mvn spring-boot:run
```

Open http://localhost:8080 — the React UI loads automatically.

**Local dev uses H2 (in-memory-style file DB) — no database setup needed.**

### Useful local URLs

| URL | Purpose |
|---|---|
| http://localhost:8080 | React frontend |
| http://localhost:8080/swagger-ui.html | Interactive API docs |
| http://localhost:8080/h2-console | H2 database console (user: `sa`, password: `password`) |

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/orders` | Create a new order |
| `GET` | `/orders` | List all orders |
| `GET` | `/orders/{orderId}` | Get one order by ID |
| `PUT` | `/orders/{orderId}` | Update an order |
| `DELETE` | `/orders/{orderId}` | Delete an order |
| `GET` | `/meals/search/{dishName}` | Search meals from TheMealDB |

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

## Project Structure

```
restaurant2/
├── Dockerfile                          # Multi-stage Docker build
├── pom.xml                             # Maven build config
├── frontend/                           # React 18 + Vite frontend
│   └── src/
│       ├── App.jsx                     # Tab navigation (Orders / Meal Search)
│       ├── pages/
│       │   ├── OrdersPage.jsx          # CRUD table with modal form
│       │   └── MealSearchPage.jsx      # Meal search + card grid
│       ├── components/
│       │   └── OrderForm.jsx           # Shared create/edit form
│       └── api/
│           ├── ordersApi.js            # HTTP calls for orders
│           └── mealsApi.js             # HTTP calls for meal search
└── src/main/java/com/restaurant/orderservice/
    ├── controller/                     # REST endpoints
    ├── service/                        # Business logic
    ├── repository/                     # JPA data access
    ├── model/                          # Order entity + enums
    ├── dto/                            # Request/Response DTOs
    ├── config/                         # CORS, WebClient, Swagger config
    └── exception/                      # Global error handler
```

---

## Order & Payment Statuses

```
PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
                                                    ↘ CANCELLED
```

| PaymentStatus | Meaning |
|---|---|
| `PENDING` | Not yet processed |
| `PAID` | Payment successful |
| `FAILED` | Payment failed |
| `REFUNDED` | Amount returned |
