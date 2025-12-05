package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.FoodDTO;
import edu.fscj.cen4940.capstone.entity.Food;
import edu.fscj.cen4940.capstone.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    // Get food by ID
    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getById(@PathVariable Integer id) {
        return foodService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search foods by partial name
    @GetMapping("/search")
    public List<FoodDTO> searchByName(@RequestParam("q") String query) {
        return foodService.searchByName(query);
    }

    // Get foods within a calorie range
    @GetMapping("/calories/range")
    public List<FoodDTO> getByCaloriesBetween(@RequestParam("min") Double minCalories,
                                              @RequestParam("max") Double maxCalories) {
        return foodService.getByCaloriesBetween(minCalories, maxCalories);
    }

    // Get foods with calories less than or equal to value
    @GetMapping("/calories/max")
    public List<FoodDTO> getByCaloriesLessThanEqual(@RequestParam("value") Double maxCalories) {
        return foodService.getByCaloriesLessThanEqual(maxCalories);
    }

    // Get foods with calories greater than or equal to value
    @GetMapping("/calories/min")
    public List<FoodDTO> getByCaloriesGreaterThanEqual(@RequestParam("value") Double minCalories) {
        return foodService.getByCaloriesGreaterThanEqual(minCalories);
    }

    // Create new food - THIS IS THE IMPORTANT CHANGE
    @PostMapping
    public FoodDTO createFood(@RequestBody FoodDTO dto) {
        // Use createFoodNameUnique instead of save
        Food food = foodService.createFoodNameUnique(dto);
        return new FoodDTO(
                food.getId(),
                food.getName(),
                food.getCalories(),
                food.getAmount(),
                food.getUnit(),
                food.getServings()
        );
    }

    // Update food
    @PutMapping("/{id}")
    public ResponseEntity<FoodDTO> updateFood(@PathVariable Integer id,
                                              @RequestBody FoodDTO dto) {
        try {
            FoodDTO updated = foodService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete food
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Integer id) {
        try {
            foodService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}