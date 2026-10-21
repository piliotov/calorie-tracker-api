package com.piliotov.calorietracker.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.MealType;
import com.piliotov.calorietracker.domain.Sex;
import com.piliotov.calorietracker.food.Food;
import com.piliotov.calorietracker.meal.MealEntry;
import com.piliotov.calorietracker.meal.MealService;
import com.piliotov.calorietracker.nutrition.NutritionCalculator;
import com.piliotov.calorietracker.profile.ProfileService;
import com.piliotov.calorietracker.profile.UserProfile;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    private static final LocalDate DAY = LocalDate.of(2025, 3, 10);

    @Mock
    private ProfileService profiles;

    @Mock
    private MealService meals;

    private SummaryService service;
    private UserProfile profile;
    private final Food oats = new Food("Oats", 389, 16.9, 66.3, 6.9);
    private final Food chicken = new Food("Chicken breast", 165, 31, 0, 3.6);

    @BeforeEach
    void setUp() {
        service = new SummaryService(profiles, meals, new NutritionCalculator());
        profile = new UserProfile("Alex", Sex.MALE, 30, 180, 80, ActivityLevel.MODERATE, Goal.MAINTAIN);
        lenient().when(profiles.get(1L)).thenReturn(profile);
    }

    @Test
    void dailySummaryGroupsByMealAndComputesRemaining() {
        when(meals.forDay(1L, DAY)).thenReturn(List.of(
                new MealEntry(profile, oats, 100, MealType.BREAKFAST, DAY),
                new MealEntry(profile, chicken, 200, MealType.LUNCH, DAY),
                new MealEntry(profile, oats, 50, MealType.LUNCH, DAY)));

        DailySummary summary = service.daily(1L, DAY);

        assertThat(summary.entries()).isEqualTo(3);
        assertThat(summary.consumed().calories()).isEqualTo(389 + 330 + 194.5);
        assertThat(summary.byMeal()).containsOnlyKeys(MealType.BREAKFAST, MealType.LUNCH);
        assertThat(summary.byMeal().get(MealType.LUNCH).calories()).isEqualTo(524.5);
        assertThat(summary.remaining().calories())
                .isEqualTo(Math.round((summary.target().calories() - 913.5) * 10) / 10.0);
        assertThat(summary.caloriePercent()).isEqualTo(33);
    }

    @Test
    void emptyDayHasZeroConsumption() {
        when(meals.forDay(1L, DAY)).thenReturn(List.of());

        DailySummary summary = service.daily(1L, DAY);

        assertThat(summary.consumed().calories()).isZero();
        assertThat(summary.remaining()).isEqualTo(summary.target());
        assertThat(summary.caloriePercent()).isZero();
    }

    @Test
    void trendCountsOnTargetDaysAndProjectsWeightChange() {
        LocalDate from = DAY.minusDays(6);
        when(meals.between(1L, from, DAY)).thenReturn(List.of(
                new MealEntry(profile, oats, 700, MealType.BREAKFAST, DAY.minusDays(1)),
                new MealEntry(profile, oats, 400, MealType.BREAKFAST, DAY)));

        TrendReport report = service.trend(1L, DAY, 7);

        assertThat(report.from()).isEqualTo(from);
        assertThat(report.daysLogged()).isEqualTo(2);
        assertThat(report.days()).extracting(TrendReport.DayTotal::date)
                .containsExactly(DAY.minusDays(1), DAY);
        assertThat(report.daysOnTarget()).isEqualTo(1);
        assertThat(report.averageCalories()).isEqualTo(2140);
        assertThat(report.projectedWeightChangeKg()).isEqualTo(-0.56);
    }

    @Test
    void trendWithoutEntriesProjectsNoChange() {
        when(meals.between(1L, DAY.minusDays(13), DAY)).thenReturn(List.of());

        TrendReport report = service.trend(1L, DAY, 14);

        assertThat(report.daysLogged()).isZero();
        assertThat(report.projectedWeightChangeKg()).isZero();
        assertThat(report.days()).isEmpty();
    }

    @Test
    void toleranceIsTenPercent() {
        assertThat(SummaryService.isOnTarget(2200, 2000)).isTrue();
        assertThat(SummaryService.isOnTarget(1800, 2000)).isTrue();
        assertThat(SummaryService.isOnTarget(2201, 2000)).isFalse();
    }
}
