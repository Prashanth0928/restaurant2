package com.restaurant.orderservice.controller;

import com.restaurant.orderservice.dto.ApiResponse;
import com.restaurant.orderservice.dto.MealDTO;
import com.restaurant.orderservice.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the meal search API which fetches data from TheMealDB
 * using WebClient — a real external internet API call.
 */
@Slf4j
@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
@Tag(name = "Meal Search (WebClient Demo)", description = "Search real dish details from TheMealDB external API using WebClient")
public class MealController {

    private final MealService mealService;

    @GetMapping("/search/{dishName}")
    @Operation(
            summary = "Search meals by dish name",
            description = "Uses Spring WebClient to call TheMealDB (a real external API on the internet) " +
                    "and returns matching dish details. " +
                    "Try: biryani, pizza, pasta, burger, sushi"
    )
    public ResponseEntity<ApiResponse<List<MealDTO>>> searchMeals(
            @Parameter(description = "Dish name to search", example = "biryani")
            @PathVariable String dishName) {

        log.info("GET /meals/search/{} — delegating to MealService via WebClient", dishName);
        List<MealDTO> meals = mealService.searchMealsByName(dishName);

        if (meals.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success("No meals found for: " + dishName, meals));
        }

        return ResponseEntity.ok(
                ApiResponse.success("Found " + meals.size() + " meal(s) for: " + dishName, meals));
    }
}