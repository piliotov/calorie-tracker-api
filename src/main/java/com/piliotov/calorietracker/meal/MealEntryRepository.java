package com.piliotov.calorietracker.meal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    @EntityGraph(attributePaths = "food")
    List<MealEntry> findByProfileIdAndDateOrderById(Long profileId, LocalDate date);

    @EntityGraph(attributePaths = "food")
    List<MealEntry> findByProfileIdAndDateBetweenOrderByDate(Long profileId, LocalDate from, LocalDate to);

    Optional<MealEntry> findByIdAndProfileId(Long id, Long profileId);
}
