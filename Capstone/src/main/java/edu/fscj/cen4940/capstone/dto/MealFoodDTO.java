package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.entity.Food;
import edu.fscj.cen4940.capstone.entity.Meal;
import jakarta.validation.constraints.NotNull;

public class MealFoodDTO {
    private Integer id;
    @NotNull private Integer mealId;
    @NotNull private Integer foodId;
    @NotNull private Double servings;


    public MealFoodDTO() {}

    public MealFoodDTO(Integer id,
                       Integer mealId,
                       Integer foodId,
                       Double servings) {
        this.id = id;
        this.mealId = mealId;
        this.foodId = foodId;
        this.servings = servings;
    }

    public MealFoodDTO(Integer mealId,
                       Integer foodId,
                       Double servings) {
        this.mealId = mealId;
        this.foodId = foodId;
        this.servings = servings;
    }

    // Getters and Setters
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMealId() { return mealId; }
    public void setMealId(Integer mealId) { this.mealId = mealId; }

    public Integer getFoodId() { return foodId; }
    public void setFoodId(Integer foodId) { this.foodId = foodId; }

    public Double getServings() { return servings; }
    public void setServings(Double servings) { this.servings = servings; }
}
