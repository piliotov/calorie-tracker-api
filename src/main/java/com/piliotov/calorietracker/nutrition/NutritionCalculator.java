package com.piliotov.calorietracker.nutrition;

import org.springframework.stereotype.Component;

@Component
public class NutritionCalculator {

    static final double KCAL_PER_GRAM_PROTEIN = 4;
    static final double KCAL_PER_GRAM_CARBS = 4;
    static final double KCAL_PER_GRAM_FAT = 9;
    static final double FAT_SHARE = 0.25;

    public double bmr(BodyMetrics m) {
        double base = 10 * m.weightKg() + 6.25 * m.heightCm() - 5 * m.age();
        return switch (m.sex()) {
            case MALE -> base + 5;
            case FEMALE -> base - 161;
        };
    }

    public double tdee(BodyMetrics m) {
        return bmr(m) * m.activityLevel().multiplier();
    }

    public Macros dailyTarget(BodyMetrics m) {
        double calories = tdee(m) * m.goal().calorieFactor();
        double protein = m.weightKg() * m.goal().proteinPerKg();
        double fat = calories * FAT_SHARE / KCAL_PER_GRAM_FAT;
        double remaining = calories - protein * KCAL_PER_GRAM_PROTEIN - fat * KCAL_PER_GRAM_FAT;
        double carbs = Math.max(0, remaining / KCAL_PER_GRAM_CARBS);
        return new Macros(calories, protein, carbs, fat).rounded();
    }

    public EnergyBreakdown breakdown(BodyMetrics m) {
        return new EnergyBreakdown(
                (int) Math.round(bmr(m)),
                (int) Math.round(tdee(m)),
                dailyTarget(m));
    }
}
