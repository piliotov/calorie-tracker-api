package com.piliotov.calorietracker.nutrition;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.Sex;

public record BodyMetrics(
        Sex sex,
        int age,
        double heightCm,
        double weightKg,
        ActivityLevel activityLevel,
        Goal goal) {
}
