package com.piliotov.calorietracker;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:${random.uuid}")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CalorieTrackerApiIntegrationTest {

    private static final String PROFILE = """
            {"name":"Alex","sex":"MALE","age":30,"heightCm":180,"weightKg":80,
             "activityLevel":"MODERATE","goal":"MAINTAIN"}
            """;

    @TestConfiguration
    static class FixedClock {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2025-03-10T12:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void calculatorReturnsEnergyBreakdown() throws Exception {
        mvc.perform(post("/api/calculator/energy").contentType(MediaType.APPLICATION_JSON).content("""
                        {"sex":"FEMALE","age":30,"heightCm":165,"weightKg":60,
                         "activityLevel":"MODERATE","goal":"MAINTAIN"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmr").value(1320))
                .andExpect(jsonPath("$.tdee").value(2046))
                .andExpect(jsonPath("$.target.protein").value(108.0));
    }

    @Test
    void invalidProfileIsRejectedWithFieldErrors() throws Exception {
        mvc.perform(post("/api/profiles").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"","sex":"MALE","age":8,"heightCm":180,"weightKg":80,
                         "activityLevel":"MODERATE","goal":"MAINTAIN"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.age").exists());
    }

    @Test
    void profileCrud() throws Exception {
        long id = createProfile();

        mvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex"));

        mvc.perform(put("/api/profiles/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(PROFILE.replace("\"weightKg\":80", "\"weightKg\":75")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(75.0));

        mvc.perform(get("/api/profiles")).andExpect(jsonPath("$", hasSize(1)));

        mvc.perform(get("/api/profiles/{id}/targets", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.target.calories", greaterThan(2000.0)));

        mvc.perform(delete("/api/profiles/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/profiles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Profile " + id + " not found"));
    }

    @Test
    void foodsAreSeededAndSearchable() throws Exception {
        mvc.perform(get("/api/foods").param("q", "chick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Chicken breast"));

        mvc.perform(post("/api/foods").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Lentils, cooked","caloriesPer100g":116,"proteinPer100g":9,
                         "carbsPer100g":20,"fatPer100g":0.4}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mvc.perform(post("/api/foods").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"oats","caloriesPer100g":389,"proteinPer100g":17,
                         "carbsPer100g":66,"fatPer100g":7}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void loggingMealsUpdatesDailySummary() throws Exception {
        long profileId = createProfile();
        long oatsId = foodId("Oats");
        long chickenId = foodId("Chicken breast");

        logMeal(profileId, oatsId, 80, "BREAKFAST");
        logMeal(profileId, chickenId, 200, "LUNCH");
        long snackId = logMeal(profileId, oatsId, 50, "SNACK");

        mvc.perform(delete("/api/profiles/{p}/meals/{m}", profileId, snackId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/profiles/{id}/meals", profileId))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date").value("2025-03-10"));

        mvc.perform(get("/api/profiles/{id}/summary", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").value(2))
                .andExpect(jsonPath("$.consumed.calories").value(641.2))
                .andExpect(jsonPath("$.byMeal.LUNCH.protein").value(62.0))
                .andExpect(jsonPath("$.caloriePercent").value(23));

        mvc.perform(get("/api/profiles/{id}/trend", profileId).param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysLogged").value(1))
                .andExpect(jsonPath("$.from").value("2025-03-04"));
    }

    @Test
    void trendRejectsOutOfRangeDays() throws Exception {
        long profileId = createProfile();

        mvc.perform(get("/api/profiles/{id}/trend", profileId).param("days", "365"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mealForUnknownFoodReturnsNotFound() throws Exception {
        long profileId = createProfile();

        mvc.perform(post("/api/profiles/{id}/meals", profileId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foodId\":9999,\"grams\":100,\"mealType\":\"DINNER\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    private long createProfile() throws Exception {
        String body = mvc.perform(post("/api/profiles").contentType(MediaType.APPLICATION_JSON).content(PROFILE))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }

    private long foodId(String name) throws Exception {
        String body = mvc.perform(get("/api/foods").param("q", name))
                .andReturn().getResponse().getContentAsString();
        JsonNode first = mapper.readTree(body).get(0);
        return first.get("id").asLong();
    }

    private long logMeal(long profileId, long foodId, double grams, String type) throws Exception {
        String body = mvc.perform(post("/api/profiles/{id}/meals", profileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foodId\":%d,\"grams\":%s,\"mealType\":\"%s\"}".formatted(foodId, grams, type)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }
}
