package com.restaurant.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the OpenAPI 3 / Swagger UI documentation.
 *
 * SpringDoc auto-scans all @RestController classes and builds the spec;
 * this bean only customises the metadata (title, description, server URL, etc.)
 * that appears at the top of the Swagger UI page.
 *
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * Raw OpenAPI JSON: http://localhost:8080/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI restaurantOrderOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Order Management API")
                        .description("""
                                REST API for managing restaurant orders.

                                Supported operations:
                                - **POST   /orders**          — Place a new order
                                - **GET    /orders/{id}**     — Retrieve order details
                                - **PUT    /orders/{id}**     — Update an existing order
                                - **DELETE /orders/{id}**     — Cancel / remove an order
                                - **GET    /orders**          — List all orders

                                All responses are wrapped in a standard `ApiResponse` envelope.
                                Errors include a structured `ErrorResponse` with per-field details.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Restaurant Dev Team")
                                .email("dev@restaurant.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ));
    }
}