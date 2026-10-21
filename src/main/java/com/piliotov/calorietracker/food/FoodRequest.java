package com.piliotov.calorietracker.food;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record FoodRequest(
        @NotBlank String name,
        @PositiveOrZero @DecimalMax("900") double caloriesPer100g,
        @PositiveOrZero @DecimalMax("100") double proteinPer100g,
        @PositiveOrZero @DecimalMax("100") double carbsPer100g,
        @PositiveOrZero @DecimalMax("100") double fatPer100g) {
}
