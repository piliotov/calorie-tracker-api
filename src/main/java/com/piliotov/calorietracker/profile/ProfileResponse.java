package com.piliotov.calorietracker.profile;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.Sex;

public record ProfileResponse(
        Long id,
        String name,
        Sex sex,
        int age,
        double heightCm,
        double weightKg,
        ActivityLevel activityLevel,
        Goal goal) {

    static ProfileResponse from(UserProfile p) {
        return new ProfileResponse(p.getId(), p.getName(), p.getSex(), p.getAge(),
                p.getHeightCm(), p.getWeightKg(), p.getActivityLevel(), p.getGoal());
    }
}
