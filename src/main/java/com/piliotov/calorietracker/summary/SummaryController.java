package com.piliotov.calorietracker.summary;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piliotov.calorietracker.meal.MealService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/profiles/{profileId}")
public class SummaryController {

    private final SummaryService summaries;
    private final MealService meals;

    public SummaryController(SummaryService summaries, MealService meals) {
        this.summaries = summaries;
        this.meals = meals;
    }

    @GetMapping("/summary")
    public DailySummary daily(@PathVariable Long profileId,
                              @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return summaries.daily(profileId, date != null ? date : meals.today());
    }

    @GetMapping("/trend")
    public TrendReport trend(@PathVariable Long profileId,
                             @RequestParam(defaultValue = "7") @Min(1) @Max(90) int days) {
        return summaries.trend(profileId, meals.today(), days);
    }
}
