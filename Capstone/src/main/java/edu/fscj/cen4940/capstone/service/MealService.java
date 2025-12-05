package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.MealDTO;
import edu.fscj.cen4940.capstone.entity.Meal;
import edu.fscj.cen4940.capstone.entity.MealFood;
import edu.fscj.cen4940.capstone.enums.MealType;
import edu.fscj.cen4940.capstone.repository.MealFoodRepository;
import edu.fscj.cen4940.capstone.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealFoodRepository mealFoodRepository;

    // Convert entity → DTO
    private MealDTO convertToDTO(Meal meal) {
        return new MealDTO(
                meal.getMealId(),
                meal.getUserId(),
                meal.getType(),
                meal.getDate()
        );
    }

    // Create new meal
    public MealDTO save(MealDTO dto) {
        Meal meal = new Meal(
                dto.getUserId(),
                dto.getType(),
                dto.getDate()
        );
        Meal saved = mealRepository.save(meal);
        return convertToDTO(saved);
    }

    // Update existing meal
    public MealDTO update(Integer mealId, MealDTO dto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        meal.setUserId(dto.getUserId());
        meal.setType(dto.getType());
        meal.setDate(dto.getDate());

        Meal saved = mealRepository.save(meal);
        return convertToDTO(saved);
    }

    // Get by ID
    public Optional<MealDTO> getById(Integer mealId) {
        return mealRepository.findById(mealId)
                .map(this::convertToDTO);
    }

    // Get all meals for a user
    public List<MealDTO> getByUserId(Integer userId) {
        return mealRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get meals by user and date
    public List<MealDTO> getByUserIdAndDate(Integer userId,
                                            LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get meals by user and meal type
    public List<MealDTO> getByUserIdAndMealType(Integer userId,
                                                MealType mealType) {
        return mealRepository.findByUserIdAndType(userId, mealType)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Get meal by user, meal type, and date
    public List<MealDTO> getByUserIdAndMealTypeAndDate(Integer userId,
                                                       MealType mealType,
                                                       LocalDate date) {
        return mealRepository.findByUserIdAndTypeAndDate(userId, mealType, date)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Placeholder for total calories of a meal (to be implemented after MealFood exists)
    public Optional<Double> getMealCalories(Integer mealId) {
        // Fetch all MealFood entries for the given meal
        List<MealFood> mealFoods = mealFoodRepository.findByMealId(mealId);

        if (mealFoods.isEmpty()) {
            return Optional.empty(); // meal not found or has no foods
        }

        double totalCalories = mealFoods.stream()
                .mapToDouble(mf -> mf.getFood().getCalories() * mf.getServings())
                .sum();

        return Optional.of(totalCalories);
    }



    // Delete meal by ID
    public void deleteById(Integer mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new RuntimeException("Meal not found");
        }
        mealRepository.deleteById(mealId);
    }
}
