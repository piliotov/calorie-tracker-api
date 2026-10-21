package com.piliotov.calorietracker.profile;

import com.piliotov.calorietracker.domain.ActivityLevel;
import com.piliotov.calorietracker.domain.Goal;
import com.piliotov.calorietracker.domain.Sex;
import com.piliotov.calorietracker.nutrition.BodyMetrics;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private int age;
    private double heightCm;
    private double weightKg;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    protected UserProfile() {
    }

    public UserProfile(String name, Sex sex, int age, double heightCm, double weightKg,
                       ActivityLevel activityLevel, Goal goal) {
        update(name, sex, age, heightCm, weightKg, activityLevel, goal);
    }

    public void update(String name, Sex sex, int age, double heightCm, double weightKg,
                       ActivityLevel activityLevel, Goal goal) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.goal = goal;
    }

    public BodyMetrics metrics() {
        return new BodyMetrics(sex, age, heightCm, weightKg, activityLevel, goal);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public Goal getGoal() {
        return goal;
    }
}
