package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.MealFoodDTO;
import edu.fscj.cen4940.capstone.entity.*;
import edu.fscj.cen4940.capstone.repository.FoodRepository;
import edu.fscj.cen4940.capstone.repository.MealFoodRepository;
import edu.fscj.cen4940.capstone.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealFoodService {
    @Autowired
    private final MealFoodRepository mealFoodRepository;
    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;

    public MealFoodService(MealFoodRepository mealFoodRepository,
                           MealRepository mealRepository,
                           FoodRepository foodRepository) {
        this.mealFoodRepository =mealFoodRepository;
        this.mealRepository =mealRepository;
        this.foodRepository = foodRepository;
    }

    private MealFoodDTO convertToDTO(MealFood mealFood) {
            return new MealFoodDTO(
                    mealFood.getId(),
                    mealFood.getMeal().getMealId(),
                    mealFood.getFood().getId(),
                    mealFood.getServings()
            );
    }

    private MealFood convertToEntity(MealFoodDTO dto) {
        MealFood mf = new MealFood();
        Meal meal = mealRepository.findById(dto.getMealId())
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        Food food = foodRepository.findById(dto.getFoodId())
                        .orElseThrow(() -> new RuntimeException("Food not found"));

        mf.setMeal(meal);
        mf.setFood(food);
        mf.setServings(dto.getServings());

        return mf;
    }

    public MealFoodDTO save(MealFoodDTO dto) {
        MealFood entity = convertToEntity(dto);
        MealFood saved = mealFoodRepository.save(entity);
        return convertToDTO(saved);
    }

    public Optional<MealFoodDTO> getById(Integer id) {
        return mealFoodRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<MealFoodDTO> getByMealId(Integer mealId) {
        return mealFoodRepository.findByMealId(mealId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MealFoodDTO> getByFoodId(Integer foodId) {
        return mealFoodRepository.findByFoodId(foodId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MealFoodDTO> getByMealIdAndFoodId(Integer mealId, Integer foodId) {
        return mealFoodRepository.findByMealIdAndFoodId(mealId, foodId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existsById(Integer id) {
        return mealFoodRepository.existsById(id);
    }

    public MealFoodDTO update(Integer id, MealFoodDTO dto) {
        MealFood existing = mealFoodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MealFood not found"));
        // Update fields from DTO
        existing.setMeal(mealRepository.findById(dto.getMealId())
                .orElseThrow(() -> new RuntimeException("Meal not found")));
        existing.setFood(foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found")));
        existing.setServings(dto.getServings());

        MealFood saved = mealFoodRepository.save(existing);
        return convertToDTO(saved);
    }

    public void deleteById(Integer id) {
        if (!mealFoodRepository.existsById(id)) {
            throw new RuntimeException("MealFood not found");
        }
        mealFoodRepository.deleteById(id);
    }
}
