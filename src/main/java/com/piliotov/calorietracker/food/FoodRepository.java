package com.piliotov.calorietracker.food;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByNameContainingIgnoreCaseOrderByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
