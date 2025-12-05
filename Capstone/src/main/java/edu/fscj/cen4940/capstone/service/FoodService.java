package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.FoodDTO;
import edu.fscj.cen4940.capstone.entity.Food;
import edu.fscj.cen4940.capstone.repository.FoodRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // Convert entity → DTO
    private FoodDTO convertToDTO(Food food) {
        return new FoodDTO(
                food.getId(),
                food.getName(),
                food.getCalories(),
                food.getAmount(),
                food.getUnit(),
                food.getServings()
        );
    }

    // Create new food
    public FoodDTO save(FoodDTO dto) {
        if (foodRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Food with this name already exists");
        }

        Food food = new Food();
        food.setName(dto.getName());
        food.setCalories(dto.getCalories());
        food.setAmount(dto.getAmount());
        food.setUnit(dto.getUnit());
        food.setServings(dto.getServings());

        Food saved = foodRepository.save(food);
        return convertToDTO(saved);
    }

    // Update existing food
    public FoodDTO update(Integer id, FoodDTO dto) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        food.setName(dto.getName());
        food.setCalories(dto.getCalories());
        food.setAmount(dto.getAmount());
        food.setUnit(dto.getUnit());
        food.setServings(dto.getServings());

        Food saved = foodRepository.save(food);
        return convertToDTO(saved);
    }

    // Get by ID
    public Optional<FoodDTO> getById(Integer id) {
        return foodRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Search by partial name (for autocomplete)
    public List<FoodDTO> searchByName(String partialName) {
        return foodRepository.findByNameContainingIgnoreCase(partialName)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get all foods within a calorie range
    public List<FoodDTO> getByCaloriesBetween(Double minCalories, Double maxCalories) {
        return foodRepository.findAllByCaloriesBetween(minCalories, maxCalories)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get all foods under or equal to a max calorie value
    public List<FoodDTO> getByCaloriesLessThanEqual(Double maxCalories) {
        return foodRepository.findByCaloriesIsLessThanEqual(maxCalories)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get all foods above or equal to a min calorie value
    public List<FoodDTO> getByCaloriesGreaterThanEqual(Double minCalories) {
        return foodRepository.findByCaloriesIsGreaterThanEqual(minCalories)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Delete by ID
    public void deleteById(Integer id) {
        if (!foodRepository.existsById(id)) {
            throw new RuntimeException("Food not found");
        }
        foodRepository.deleteById(id);
    }

    @Transactional
    public Food createFoodNameUnique(FoodDTO dto) {
        String name = dto.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Food name is required");
        }

        // If the food already exists (case-insensitive), return it as-is
        Optional<Food> existing = foodRepository.findByNameIgnoreCase(name.trim());
        if (existing.isPresent()) {
            return existing.get();
        }

        // Otherwise create a new row
        Food f = new Food();
        f.setName(name.trim());
        f.setCalories(dto.getCalories());
        f.setServings(dto.getServings());
        f.setAmount(dto.getAmount());
        f.setUnit(dto.getUnit());
        return foodRepository.save(f);
    }
}