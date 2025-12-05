package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.MeasurementUnit;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FoodDTO {
    private Integer id;
    @NotBlank private String name;
    @NotNull private Double calories;
    private Double amount;
    private MeasurementUnit unit;
    private Double servings;

    public FoodDTO() {}

    public FoodDTO(String name,
                Double calories) {
        this.name = name;
        this.calories = calories;
    }

    public FoodDTO(String name,
                Double calories,
                Double amount,
                MeasurementUnit unit,
                Double servings) {
        this.name = name;
        this.calories = calories;
        this.amount = amount;
        this.unit = unit;
        this.servings = servings;
    }

    public FoodDTO(Integer id,
                   String name,
                   Double calories,
                   Double amount,
                   MeasurementUnit unit,
                   Double servings) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.amount = amount;
        this.unit = unit;
        this.servings = servings;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories;  }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public MeasurementUnit getUnit() { return unit; }
    public void setUnit(MeasurementUnit unit) { this.unit = unit; }

    public Double getServings() { return servings; }
    public void setServings(Double servings) { this.servings = servings; }
}
