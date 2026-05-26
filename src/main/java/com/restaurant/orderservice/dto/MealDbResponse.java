package com.restaurant.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper that matches the exact JSON structure TheMealDB returns:
 *
 * {
 *   "meals": [
 *     { "idMeal": "...", "strMeal": "Biryani", ... },
 *     { "idMeal": "...", "strMeal": "Chicken Biryani", ... }
 *   ]
 * }
 *
 * Jackson maps the "meals" JSON array directly into this List<MealDTO>.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDbResponse {
    private List<MealDTO> meals;
}