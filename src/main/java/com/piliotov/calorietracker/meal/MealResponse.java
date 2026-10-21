package com.piliotov.calorietracker.meal;

import java.time.LocalDate;

import com.piliotov.calorietracker.domain.MealType;
import com.piliotov.calorietracker.nutrition.Macros;

public record MealResponse(
        Long id,
        Long foodId,
        String foodName,
        double grams,
        MealType mealType,
        LocalDate date,
        Macros macros) {

    static MealResponse from(MealEntry e) {
        return new MealResponse(e.getId(), e.getFood().getId(), e.getFood().getName(), e.getGrams(),
                e.getMealType(), e.getDate(), e.macros().rounded());
    }
}
