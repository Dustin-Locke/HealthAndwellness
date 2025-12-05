package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.MealDTO;
import edu.fscj.cen4940.capstone.enums.MealType;
import edu.fscj.cen4940.capstone.service.MealService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    // Get meal by ID
    @GetMapping("/{id}")
    public ResponseEntity<MealDTO> getById(@PathVariable Integer id) {
        return mealService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all meals for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealDTO>> getByUserId(@PathVariable Integer userId) {
        List<MealDTO> meals = mealService.getByUserId(userId);
        return ResponseEntity.ok(meals); // always 200, empty list if no meals
    }

    // Get meals by user and date
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<MealDTO>> getByUserIdAndDate(@PathVariable Integer userId,
                                                            @PathVariable LocalDate date) {
        List<MealDTO> meals = mealService.getByUserIdAndDate(userId, date);
        return ResponseEntity.ok(meals); // always 200, empty list if no meals
    }

    // Get meals by user and meal type
    @GetMapping("/user/{userId}/type/{mealType}")
    public ResponseEntity<List<MealDTO>> getByUserIdAndMealType(@PathVariable Integer userId,
                                                                @PathVariable MealType mealType) {
        List<MealDTO> meals = mealService.getByUserIdAndMealType(userId, mealType);
        return ResponseEntity.ok(meals); // always 200, empty list if no meals
    }

    // Get meals by user, meal type, and date
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/type/{mealType}/date/{date}")
    public ResponseEntity<List<MealDTO>> getByUserIdAndMealTypeAndDate(@PathVariable Integer userId,
                                                                       @PathVariable MealType mealType,
                                                                       @PathVariable LocalDate date) {
        List<MealDTO> meals = mealService.getByUserIdAndMealTypeAndDate(userId, mealType, date);
        return ResponseEntity.ok(meals); // always 200, empty list if no meals
    }

    // Create a new meal
    @PostMapping
    public ResponseEntity<MealDTO> createMeal(@RequestBody @Valid MealDTO dto) {
        MealDTO saved = mealService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update existing meal
    @PutMapping("/{id}")
    public ResponseEntity<MealDTO> updateMeal(@PathVariable Integer id,
                                              @RequestBody @Valid MealDTO dto) {
        try {
            MealDTO updated = mealService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete meal
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Integer id) {
        try {
            mealService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/calories")
    public ResponseEntity<Double> getMealCalories(@PathVariable Integer id) {
        Optional<Double> calories = mealService.getMealCalories(id);
        return calories.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
