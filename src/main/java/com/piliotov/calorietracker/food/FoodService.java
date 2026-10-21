package com.piliotov.calorietracker.food;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piliotov.calorietracker.web.NotFoundException;

@Service
@Transactional(readOnly = true)
public class FoodService {

    private final FoodRepository repository;

    public FoodService(FoodRepository repository) {
        this.repository = repository;
    }

    public List<Food> search(String query) {
        if (query == null || query.isBlank()) {
            return repository.findAll(Sort.by("name"));
        }
        return repository.findByNameContainingIgnoreCaseOrderByName(query.trim());
    }

    public Food get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Food", id));
    }

    @Transactional
    public Food create(FoodRequest r) {
        if (repository.existsByNameIgnoreCase(r.name())) {
            throw new DuplicateFoodException(r.name());
        }
        return repository.save(new Food(r.name(), r.caloriesPer100g(), r.proteinPer100g(),
                r.carbsPer100g(), r.fatPer100g()));
    }

    @Transactional
    public Food update(Long id, FoodRequest r) {
        Food food = get(id);
        food.update(r.name(), r.caloriesPer100g(), r.proteinPer100g(), r.carbsPer100g(), r.fatPer100g());
        return food;
    }
}
