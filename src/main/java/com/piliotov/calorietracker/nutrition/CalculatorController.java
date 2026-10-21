package com.piliotov.calorietracker.nutrition;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final NutritionCalculator calculator;

    public CalculatorController(NutritionCalculator calculator) {
        this.calculator = calculator;
    }

    @PostMapping("/energy")
    public EnergyBreakdown energy(@Valid @RequestBody CalculatorRequest request) {
        return calculator.breakdown(request.toMetrics());
    }
}
