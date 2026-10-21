package com.piliotov.calorietracker.nutrition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.Sex;

class NutritionCalculatorTest {

    private final NutritionCalculator calculator = new NutritionCalculator();

    @ParameterizedTest
    @CsvSource({
            "MALE,   30, 180, 80, 1780",
            "FEMALE, 30, 165, 60, 1320.25",
            "MALE,   50, 175, 90, 1748.75",
            "FEMALE, 22, 170, 55, 1341.5"
    })
    void bmrFollowsMifflinStJeor(Sex sex, int age, double height, double weight, double expected) {
        BodyMetrics m = new BodyMetrics(sex, age, height, weight, ActivityLevel.SEDENTARY, Goal.MAINTAIN);

        assertThat(calculator.bmr(m)).isCloseTo(expected, within(0.01));
    }

    @ParameterizedTest
    @CsvSource({
            "SEDENTARY,   2136",
            "LIGHT,       2447.5",
            "MODERATE,    2759",
            "ACTIVE,      3070.5",
            "VERY_ACTIVE, 3382"
    })
    void tdeeAppliesActivityMultiplier(ActivityLevel level, double expected) {
        BodyMetrics m = new BodyMetrics(Sex.MALE, 30, 180, 80, level, Goal.MAINTAIN);

        assertThat(calculator.tdee(m)).isCloseTo(expected, within(0.01));
    }

    @Test
    void cuttingTargetIsTwentyPercentBelowTdee() {
        BodyMetrics m = new BodyMetrics(Sex.MALE, 30, 180, 80, ActivityLevel.MODERATE, Goal.LOSE);

        assertThat(calculator.dailyTarget(m).calories()).isCloseTo(2759 * 0.8, within(0.1));
    }

    @Test
    void bulkingTargetIsTenPercentAboveTdee() {
        BodyMetrics m = new BodyMetrics(Sex.MALE, 30, 180, 80, ActivityLevel.MODERATE, Goal.GAIN);

        assertThat(calculator.dailyTarget(m).calories()).isCloseTo(2759 * 1.1, within(0.1));
    }

    @Test
    void macrosAddUpToCalorieTarget() {
        BodyMetrics m = new BodyMetrics(Sex.FEMALE, 28, 168, 62, ActivityLevel.LIGHT, Goal.MAINTAIN);

        Macros target = calculator.dailyTarget(m);
        double fromMacros = target.protein() * 4 + target.carbs() * 4 + target.fat() * 9;

        assertThat(fromMacros).isCloseTo(target.calories(), within(2.0));
        assertThat(target.protein()).isCloseTo(62 * 1.8, within(0.1));
        assertThat(target.fat() * 9 / target.calories()).isCloseTo(0.25, within(0.01));
    }

    @Test
    void carbsNeverGoNegative() {
        BodyMetrics m = new BodyMetrics(Sex.FEMALE, 80, 150, 120, ActivityLevel.SEDENTARY, Goal.LOSE);

        assertThat(calculator.dailyTarget(m).carbs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void breakdownRoundsBmrAndTdee() {
        BodyMetrics m = new BodyMetrics(Sex.FEMALE, 30, 165, 60, ActivityLevel.MODERATE, Goal.MAINTAIN);

        EnergyBreakdown result = calculator.breakdown(m);

        assertThat(result.bmr()).isEqualTo(1320);
        assertThat(result.tdee()).isEqualTo(2046);
    }
}
