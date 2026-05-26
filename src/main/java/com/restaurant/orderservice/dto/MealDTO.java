package com.restaurant.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds the meal details we care about from TheMealDB API response.
 *
 * TheMealDB returns many fields (30+). We only pick the useful ones.
 * @JsonProperty maps the API's field name (e.g. "strMeal") to our cleaner name (e.g. "mealName").
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Meal details fetched from TheMealDB external API")
public class MealDTO {

    @JsonProperty("idMeal")
    @Schema(description = "Unique meal ID from TheMealDB", example = "52772")
    private String mealId;

    @JsonProperty("strMeal")
    @Schema(description = "Name of the meal", example = "Chicken Biryani")
    private String mealName;

    @JsonProperty("strCategory")
    @Schema(description = "Food category", example = "Chicken")
    private String category;

    @JsonProperty("strArea")
    @Schema(description = "Cuisine origin country", example = "Indian")
    private String area;

    @JsonProperty("strInstructions")
    @Schema(description = "How to cook the dish")
    private String instructions;

    @JsonProperty("strMealThumb")
    @Schema(description = "URL of the dish image")
    private String imageUrl;

    @JsonProperty("strTags")
    @Schema(description = "Tags for the meal", example = "Curry,Meat")
    private String tags;

    @JsonProperty("strYoutube")
    @Schema(description = "YouTube video link for the recipe")
    private String youtubeLink;
}
