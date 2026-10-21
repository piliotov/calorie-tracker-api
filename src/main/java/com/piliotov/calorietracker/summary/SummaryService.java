package com.piliotov.calorietracker.summary;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.piliotov.calorietracker.domain.MealType;
import com.piliotov.calorietracker.meal.MealEntry;
import com.piliotov.calorietracker.meal.MealService;
import com.piliotov.calorietracker.nutrition.Macros;
import com.piliotov.calorietracker.nutrition.NutritionCalculator;
import com.piliotov.calorietracker.profile.ProfileService;
import com.piliotov.calorietracker.profile.UserProfile;

@Service
public class SummaryService {

    static final double KCAL_PER_KG_BODY_WEIGHT = 7700;
    static final double ON_TARGET_TOLERANCE = 0.10;

    private final ProfileService profiles;
    private final MealService meals;
    private final NutritionCalculator calculator;

    public SummaryService(ProfileService profiles, MealService meals, NutritionCalculator calculator) {
        this.profiles = profiles;
        this.meals = meals;
        this.calculator = calculator;
    }

    public DailySummary daily(Long profileId, LocalDate date) {
        UserProfile profile = profiles.get(profileId);
        Macros target = calculator.dailyTarget(profile.metrics());
        List<MealEntry> entries = meals.forDay(profileId, date);

        Map<MealType, Macros> byMeal = new EnumMap<>(MealType.class);
        Macros consumed = Macros.ZERO;
        for (MealEntry entry : entries) {
            Macros m = entry.macros();
            byMeal.merge(entry.getMealType(), m, Macros::plus);
            consumed = consumed.plus(m);
        }
        byMeal.replaceAll((type, m) -> m.rounded());

        int percent = (int) Math.round(consumed.calories() / target.calories() * 100);
        return new DailySummary(date, target, consumed.rounded(), target.minus(consumed).rounded(),
                percent, byMeal, entries.size());
    }

    public TrendReport trend(Long profileId, LocalDate to, int days) {
        UserProfile profile = profiles.get(profileId);
        LocalDate from = to.minusDays(days - 1L);
        double target = calculator.dailyTarget(profile.metrics()).calories();
        double tdee = calculator.tdee(profile.metrics());

        Map<LocalDate, Macros> totals = new TreeMap<>();
        for (MealEntry entry : meals.between(profileId, from, to)) {
            totals.merge(entry.getDate(), entry.macros(), Macros::plus);
        }

        List<TrendReport.DayTotal> dayTotals = totals.entrySet().stream()
                .map(e -> new TrendReport.DayTotal(
                        e.getKey(),
                        (int) Math.round(e.getValue().calories()),
                        e.getValue().rounded().protein(),
                        isOnTarget(e.getValue().calories(), target)))
                .toList();

        int logged = dayTotals.size();
        double avgCalories = totals.values().stream().mapToDouble(Macros::calories).average().orElse(0);
        double avgProtein = totals.values().stream().mapToDouble(Macros::protein).average().orElse(0);
        int onTarget = (int) dayTotals.stream().filter(TrendReport.DayTotal::onTarget).count();
        double projected = logged == 0 ? 0 : (avgCalories - tdee) * days / KCAL_PER_KG_BODY_WEIGHT;

        return new TrendReport(from, to, (int) Math.round(target), (int) Math.round(avgCalories),
                Math.round(avgProtein * 10) / 10.0, logged, onTarget,
                Math.round(projected * 100) / 100.0, dayTotals);
    }

    static boolean isOnTarget(double calories, double target) {
        return Math.abs(calories - target) <= target * ON_TARGET_TOLERANCE;
    }
}
