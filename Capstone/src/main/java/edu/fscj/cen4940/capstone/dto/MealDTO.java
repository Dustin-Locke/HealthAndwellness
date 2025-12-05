package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.MealType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MealDTO {


    private Integer id;
    @NotNull private Integer userId;
    @NotNull private MealType type;
    @NotNull private LocalDate date;

    public MealDTO() {}

    public MealDTO(Integer userId, MealType type, LocalDate date) {
        this.userId = userId;
        this.type = type;
        this.date = date;
    }

    public MealDTO(Integer mealId, Integer userId, MealType type, LocalDate date) {
        this.id = mealId;
        this.userId = userId;
        this.type = type;
        this.date = date;
    }

    // Getters and Setters
    public Integer getMealId() { return id; }
    public void setMealId(Integer mealId) { this.id = mealId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public MealType getType() { return type; }
    public void setType(MealType type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
