package com.piliotov.calorietracker.meal;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles/{profileId}/meals")
public class MealController {

    private final MealService service;

    public MealController(MealService service) {
        this.service = service;
    }

    @GetMapping
    public List<MealResponse> list(@PathVariable Long profileId,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate day = date != null ? date : service.today();
        return service.forDay(profileId, day).stream().map(MealResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<MealResponse> log(@PathVariable Long profileId, @Valid @RequestBody MealRequest request) {
        MealResponse created = MealResponse.from(service.log(profileId, request));
        URI location = URI.create("/api/profiles/" + profileId + "/meals/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> delete(@PathVariable Long profileId, @PathVariable Long mealId) {
        service.delete(profileId, mealId);
        return ResponseEntity.noContent().build();
    }
}
