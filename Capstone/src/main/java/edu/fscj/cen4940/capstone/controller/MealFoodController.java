package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.MealFoodDTO;
import edu.fscj.cen4940.capstone.service.MealFoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meal-food")
public class MealFoodController {

    @Autowired
    private MealFoodService mealFoodService;

    @PostMapping
    public ResponseEntity<MealFoodDTO> save(@RequestBody @Valid MealFoodDTO dto) {
        MealFoodDTO saved = mealFoodService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealFoodDTO> getById(@PathVariable Integer id) {
        Optional<MealFoodDTO> mfOptional = mealFoodService.getById(id);
        return mfOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<MealFoodDTO>> getByMealId(@PathVariable Integer mealId) {
        List<MealFoodDTO> mfList = mealFoodService.getByMealId(mealId);
        return ResponseEntity.ok(mfList); // always 200, empty list if no meals
    }

    @GetMapping("/food/{foodId}")
    public ResponseEntity<List<MealFoodDTO>> getByFoodId(@PathVariable Integer foodId) {
        List<MealFoodDTO> mfList = mealFoodService.getByFoodId(foodId);
        return ResponseEntity.ok(mfList); // always 200, empty list if no meals
    }

    @GetMapping("/meal/{mealId}/food/{foodId}")
    public ResponseEntity<List<MealFoodDTO>> getByMealIdAndFoodId(
            @PathVariable Integer mealId,
            @PathVariable Integer foodId) {
        List<MealFoodDTO> mfList = mealFoodService.getByMealIdAndFoodId(mealId, foodId);
        return ResponseEntity.ok(mfList); // always 200, empty list if no meals
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealFoodDTO> update(@PathVariable Integer id,
                                              @RequestBody @Valid MealFoodDTO dto) {
        try {
            MealFoodDTO updated = mealFoodService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!mealFoodService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        mealFoodService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
