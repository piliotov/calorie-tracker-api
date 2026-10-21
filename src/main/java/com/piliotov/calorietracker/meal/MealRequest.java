package com.piliotov.calorietracker.meal;

import java.time.LocalDate;

import com.piliotov.calorietracker.domain.MealType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record MealRequest(
        @NotNull Long foodId,
        @Positive @DecimalMax("5000") double grams,
        @NotNull MealType mealType,
        @PastOrPresent LocalDate date) {
}
