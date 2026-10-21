package com.piliotov.calorietracker.food;

public record FoodResponse(
        Long id,
        String name,
        double caloriesPer100g,
        double proteinPer100g,
        double carbsPer100g,
        double fatPer100g) {

    static FoodResponse from(Food f) {
        return new FoodResponse(f.getId(), f.getName(), f.getCaloriesPer100g(),
                f.getProteinPer100g(), f.getCarbsPer100g(), f.getFatPer100g());
    }
}
