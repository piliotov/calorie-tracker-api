# calorie-tracker-api

[![CI](https://github.com/piliotov/calorie-tracker-api/actions/workflows/ci.yml/badge.svg)](https://github.com/piliotov/calorie-tracker-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

REST API for tracking calories and macronutrients. It calculates personal energy needs from body
metrics, sets daily macro targets based on a goal, logs meals and reports progress per day and over
time.

## Features

- BMR using the Mifflin-St Jeor equation and TDEE from activity level
- Daily calorie target adjusted for the goal (lose -20%, maintain, gain +10%)
- Macro split: protein by body weight, 25% of energy from fat, carbs fill the rest
- Food database with search (seeded with common foods, extendable through the API)
- Meal logging per profile and day, grouped by breakfast, lunch, dinner and snack
- Daily summary with consumed, remaining and percentage of target
- Trend report over up to 90 days with adherence and projected weight change
- Validation with RFC 7807 problem responses
- OpenAPI docs, Actuator health probes and Prometheus metrics

## Tech stack

Java 21, Spring Boot 3.5, Spring Data JPA, H2 / PostgreSQL, JUnit 5, Mockito, AssertJ, JaCoCo,
Docker, GitHub Actions.

## Running locally

```bash
./mvnw spring-boot:run      # or: mvn spring-boot:run
```

The API starts on `http://localhost:8080` with an in-memory H2 database.
Swagger UI is available at `http://localhost:8080/docs`.

With PostgreSQL:

```bash
docker compose up --build
```

## API overview

| Method | Path | Description |
| ------ | ---- | ----------- |
| POST | `/api/calculator/energy` | BMR, TDEE and macro targets without saving anything |
| GET, POST | `/api/profiles` | List or create profiles |
| GET, PUT, DELETE | `/api/profiles/{id}` | Read, update or delete a profile |
| GET | `/api/profiles/{id}/targets` | Energy breakdown for a profile |
| GET, POST | `/api/foods?q=` | Search or add foods |
| GET, PUT | `/api/foods/{id}` | Read or update a food |
| GET, POST | `/api/profiles/{id}/meals?date=` | List or log meals for a day |
| DELETE | `/api/profiles/{id}/meals/{mealId}` | Remove a meal entry |
| GET | `/api/profiles/{id}/summary?date=` | Daily totals against the target |
| GET | `/api/profiles/{id}/trend?days=7` | Averages and adherence over a period |

### Example

```bash
curl -X POST localhost:8080/api/calculator/energy \
  -H "Content-Type: application/json" \
  -d '{"sex":"FEMALE","age":30,"heightCm":165,"weightKg":60,"activityLevel":"MODERATE","goal":"MAINTAIN"}'
```

```json
{
  "bmr": 1320,
  "tdee": 2046,
  "target": { "calories": 2046.4, "protein": 108.0, "carbs": 275.7, "fat": 56.8 }
}
```

## Tests

```bash
mvn verify
```

Runs unit tests for the nutrition logic, service tests with Mockito and full HTTP tests with MockMvc.
The build fails if line coverage drops below 80%. The coverage report is written to
`target/site/jacoco/index.html`.

## CI/CD

Every push and pull request runs the test suite. Pushes to `main` also build the Docker image and
publish it to GitHub Container Registry as `ghcr.io/piliotov/calorie-tracker-api`.

## License

MIT
