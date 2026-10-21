package com.piliotov.calorietracker.summary;

import java.time.LocalDate;
import java.util.List;

public record TrendReport(
        LocalDate from,
        LocalDate to,
        int targetCalories,
        int averageCalories,
        double averageProtein,
        int daysLogged,
        int daysOnTarget,
        double projectedWeightChangeKg,
        List<DayTotal> days) {

    public record DayTotal(LocalDate date, int calories, double protein, boolean onTarget) {
    }
}
