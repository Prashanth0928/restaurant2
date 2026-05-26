package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.MealDbResponse;
import com.restaurant.orderservice.dto.MealDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Uses WebClient to call TheMealDB — a REAL external API on the internet.
 *
 * API we are calling:
 * https://www.themealdb.com/api/json/v1/1/search.php?s=biryani
 *
 * Flow:
 * Our App  ──WebClient──►  https://www.themealdb.com  ──►  returns meal list
 *
 * @Qualifier("mealDbWebClient") tells Spring:
 * "inject the WebClient bean named mealDbWebClient, not the localhost one"
 */
@Slf4j
@Service
public class MealService {

    private final WebClient mealDbWebClient;

    public MealService(@Qualifier("mealDbWebClient") WebClient mealDbWebClient) {
        this.mealDbWebClient = mealDbWebClient;
    }

    public List<MealDTO> searchMealsByName(String dishName) {
        log.info("WebClient → calling TheMealDB API to search for: {}", dishName);

        MealDbResponse response = mealDbWebClient
                .get()
                .uri("/api/json/v1/1/search.php?s={dishName}", dishName)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("TheMealDB returned 4xx error")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("TheMealDB server is down")))
                .bodyToMono(MealDbResponse.class)
                .block();

        log.info("WebClient ← TheMealDB responded successfully for: {}", dishName);

        if (response == null || response.getMeals() == null) {
            log.warn("No meals found for dish: {}", dishName);
            return Collections.emptyList();
        }

        return response.getMeals();
    }
}