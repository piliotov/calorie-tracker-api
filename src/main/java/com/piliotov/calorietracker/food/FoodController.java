package com.piliotov.calorietracker.food;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService service;

    public FoodController(FoodService service) {
        this.service = service;
    }

    @GetMapping
    public List<FoodResponse> search(@RequestParam(required = false) String q) {
        return service.search(q).stream().map(FoodResponse::from).toList();
    }

    @GetMapping("/{id}")
    public FoodResponse get(@PathVariable Long id) {
        return FoodResponse.from(service.get(id));
    }

    @PostMapping
    public ResponseEntity<FoodResponse> create(@Valid @RequestBody FoodRequest request) {
        FoodResponse created = FoodResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/foods/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public FoodResponse update(@PathVariable Long id, @Valid @RequestBody FoodRequest request) {
        return FoodResponse.from(service.update(id, request));
    }
}
