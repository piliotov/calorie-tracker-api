package com.piliotov.calorietracker.profile;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.piliotov.calorietracker.nutrition.EnergyBreakdown;
import com.piliotov.calorietracker.nutrition.NutritionCalculator;
import com.piliotov.calorietracker.web.NotFoundException;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserProfileRepository repository;
    private final NutritionCalculator calculator;

    public ProfileService(UserProfileRepository repository, NutritionCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    public List<UserProfile> findAll() {
        return repository.findAll();
    }

    public UserProfile get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Profile", id));
    }

    @Transactional
    public UserProfile create(ProfileRequest r) {
        return repository.save(new UserProfile(r.name(), r.sex(), r.age(), r.heightCm(),
                r.weightKg(), r.activityLevel(), r.goal()));
    }

    @Transactional
    public UserProfile update(Long id, ProfileRequest r) {
        UserProfile profile = get(id);
        profile.update(r.name(), r.sex(), r.age(), r.heightCm(), r.weightKg(),
                r.activityLevel(), r.goal());
        return profile;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }

    public EnergyBreakdown targets(Long id) {
        return calculator.breakdown(get(id).metrics());
    }
}
