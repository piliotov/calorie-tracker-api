package com.piliotov.calorietracker.food;

import com.piliotov.calorietracker.nutrition.Macros;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private double caloriesPer100g;
    private double proteinPer100g;
    private double carbsPer100g;
    private double fatPer100g;

    protected Food() {
    }

    public Food(String name, double calories, double protein, double carbs, double fat) {
        update(name, calories, protein, carbs, fat);
    }

    public void update(String name, double calories, double protein, double carbs, double fat) {
        this.name = name;
        this.caloriesPer100g = calories;
        this.proteinPer100g = protein;
        this.carbsPer100g = carbs;
        this.fatPer100g = fat;
    }

    public Macros macrosFor(double grams) {
        return new Macros(caloriesPer100g, proteinPer100g, carbsPer100g, fatPer100g).scale(grams / 100);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }
}
