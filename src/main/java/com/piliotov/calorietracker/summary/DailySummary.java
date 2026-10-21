package com.piliotov.calorietracker.summary;

import java.time.LocalDate;
import java.util.Map;

import com.piliotov.calorietracker.domain.MealType;
import com.piliotov.calorietracker.nutrition.Macros;

public record DailySummary(
        LocalDate date,
        Macros target,
        Macros consumed,
        Macros remaining,
        int caloriePercent,
        Map<MealType, Macros> byMeal,
        int entries) {
}
