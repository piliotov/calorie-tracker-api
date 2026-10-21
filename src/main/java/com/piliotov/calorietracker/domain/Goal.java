package com.piliotov.calorietracker.domain;

public enum Goal {
    LOSE(0.80, 2.2),
    MAINTAIN(1.00, 1.8),
    GAIN(1.10, 2.0);

    private final double calorieFactor;
    private final double proteinPerKg;

    Goal(double calorieFactor, double proteinPerKg) {
        this.calorieFactor = calorieFactor;
        this.proteinPerKg = proteinPerKg;
    }

    public double calorieFactor() {
        return calorieFactor;
    }

    public double proteinPerKg() {
        return proteinPerKg;
    }
}
