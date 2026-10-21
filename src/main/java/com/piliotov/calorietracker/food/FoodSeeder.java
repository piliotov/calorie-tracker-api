package com.piliotov.calorietracker.food;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-foods", havingValue = "true", matchIfMissing = true)
class FoodSeeder implements ApplicationRunner {

    private final FoodRepository repository;

    FoodSeeder(FoodRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Food("Chicken breast", 165, 31.0, 0.0, 3.6),
                new Food("White rice, cooked", 130, 2.7, 28.2, 0.3),
                new Food("Oats", 389, 16.9, 66.3, 6.9),
                new Food("Egg", 155, 13.0, 1.1, 11.0),
                new Food("Banana", 89, 1.1, 22.8, 0.3),
                new Food("Greek yogurt", 59, 10.0, 3.6, 0.4),
                new Food("Salmon", 208, 20.0, 0.0, 13.0),
                new Food("Broccoli", 34, 2.8, 6.6, 0.4),
                new Food("Almonds", 579, 21.2, 21.6, 49.9),
                new Food("Olive oil", 884, 0.0, 0.0, 100.0),
                new Food("Whole wheat bread", 247, 13.0, 41.0, 3.4),
                new Food("Potato, boiled", 87, 1.9, 20.1, 0.1)));
    }
}
