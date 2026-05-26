package com.restaurant.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Restaurant Order Management Service.
 *
 * @SpringBootApplication combines:
 *   - @Configuration       : marks this class as a Spring config source
 *   - @EnableAutoConfiguration : auto-configures Spring beans based on classpath
 *   - @ComponentScan       : scans sub-packages for @Component, @Service, @Repository, etc.
 */
@SpringBootApplication
public class RestaurantOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantOrderServiceApplication.class, args);
    }
}
