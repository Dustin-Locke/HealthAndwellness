package edu.fscj.cen4940.capstone.entity;

import edu.fscj.cen4940.capstone.enums.MealType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "meal")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType type;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    public Meal() {}

    public Meal(Integer userId, MealType mealType, LocalDate date) {
        this.userId = userId;
        this.type = mealType;
        this.date = date;
    }

    // Getters and Setters
    public Integer getMealId() { return id; }
    public void setMealId(Integer mealId) { this.id = mealId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public MealType getType() { return type; }
    public void setType(MealType mealType) { this.type = mealType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
