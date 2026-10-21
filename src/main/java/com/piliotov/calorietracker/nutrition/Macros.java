package com.piliotov.calorietracker.nutrition;

public record Macros(double calories, double protein, double carbs, double fat) {

    public static final Macros ZERO = new Macros(0, 0, 0, 0);

    public Macros plus(Macros other) {
        return new Macros(
                calories + other.calories,
                protein + other.protein,
                carbs + other.carbs,
                fat + other.fat);
    }

    public Macros minus(Macros other) {
        return new Macros(
                calories - other.calories,
                protein - other.protein,
                carbs - other.carbs,
                fat - other.fat);
    }

    public Macros scale(double factor) {
        return new Macros(calories * factor, protein * factor, carbs * factor, fat * factor);
    }

    public Macros rounded() {
        return new Macros(round(calories), round(protein), round(carbs), round(fat));
    }

    private static double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
