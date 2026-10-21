package com.piliotov.calorietracker.meal;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.piliotov.calorietracker.domain.MealType;
import com.piliotov.calorietracker.food.Food;
import com.piliotov.calorietracker.nutrition.Macros;
import com.piliotov.calorietracker.profile.UserProfile;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(columnList = "profile_id, date"))
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserProfile profile;

    @ManyToOne(optional = false)
    private Food food;

    private double grams;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    private LocalDate date;

    protected MealEntry() {
    }

    public MealEntry(UserProfile profile, Food food, double grams, MealType mealType, LocalDate date) {
        this.profile = profile;
        this.food = food;
        this.grams = grams;
        this.mealType = mealType;
        this.date = date;
    }

    public Macros macros() {
        return food.macrosFor(grams);
    }

    public Long getId() {
        return id;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Food getFood() {
        return food;
    }

    public double getGrams() {
        return grams;
    }

    public MealType getMealType() {
        return mealType;
    }

    public LocalDate getDate() {
        return date;
    }
}
