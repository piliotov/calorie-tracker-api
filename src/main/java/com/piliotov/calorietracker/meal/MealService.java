package com.piliotov.calorietracker.meal;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piliotov.calorietracker.food.FoodService;
import com.piliotov.calorietracker.profile.ProfileService;
import com.piliotov.calorietracker.web.NotFoundException;

@Service
@Transactional(readOnly = true)
public class MealService {

    private final MealEntryRepository repository;
    private final ProfileService profiles;
    private final FoodService foods;
    private final Clock clock;

    public MealService(MealEntryRepository repository, ProfileService profiles, FoodService foods, Clock clock) {
        this.repository = repository;
        this.profiles = profiles;
        this.foods = foods;
        this.clock = clock;
    }

    @Transactional
    public MealEntry log(Long profileId, MealRequest r) {
        LocalDate date = r.date() != null ? r.date() : LocalDate.now(clock);
        return repository.save(new MealEntry(profiles.get(profileId), foods.get(r.foodId()),
                r.grams(), r.mealType(), date));
    }

    public List<MealEntry> forDay(Long profileId, LocalDate date) {
        profiles.get(profileId);
        return repository.findByProfileIdAndDateOrderById(profileId, date);
    }

    public List<MealEntry> between(Long profileId, LocalDate from, LocalDate to) {
        profiles.get(profileId);
        return repository.findByProfileIdAndDateBetweenOrderByDate(profileId, from, to);
    }

    @Transactional
    public void delete(Long profileId, Long mealId) {
        MealEntry entry = repository.findByIdAndProfileId(mealId, profileId)
                .orElseThrow(() -> new NotFoundException("Meal", mealId));
        repository.delete(entry);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }
}
