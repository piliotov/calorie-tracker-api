package com.piliotov.calorietracker.profile;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.Sex;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileRequest(
        @NotBlank String name,
        @NotNull Sex sex,
        @Min(14) @Max(100) int age,
        @DecimalMin("100") @DecimalMax("250") double heightCm,
        @DecimalMin("30") @DecimalMax("300") double weightKg,
        @NotNull ActivityLevel activityLevel,
        @NotNull Goal goal) {
}
