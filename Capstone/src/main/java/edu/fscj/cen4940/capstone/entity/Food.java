package edu.fscj.cen4940.capstone.entity;

import edu.fscj.cen4940.capstone.enums.MeasurementUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Double calories;

    @Column()
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column()
    private MeasurementUnit unit;

    @Column()
    private Double servings;

    public Food() {}

    public Food(String name,
                Double calories) {
        this.name = name;
        this.calories = calories;
    }

    public Food(String name,
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

    // Getters and Setters
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

