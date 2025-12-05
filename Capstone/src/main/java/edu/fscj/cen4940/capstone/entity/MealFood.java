package edu.fscj.cen4940.capstone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "meal_food")
public class MealFood {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Meal meal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Food food;

    @NotNull
    @Column(nullable=false)
    private Double servings;

    public MealFood() {}

    public MealFood(Meal meal,
                    Food food,
                    Double servings) {
        this.meal = meal;
        this.food = food;
        this.servings = servings;
    }

    // Getters and Setters
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }

    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }

    public Food getFood() { return food; }
    public void setFood(Food food) { this.food = food; }

    public Double getServings() { return servings; }
    public void setServings(Double servings) { this.servings = servings; }
}
