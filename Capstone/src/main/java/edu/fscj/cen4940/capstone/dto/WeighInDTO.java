package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.MeasurementSystem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class WeighInDTO {

    private Integer userId;

    private MeasurementSystem measurement;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Weight is required")
    @Positive(message = "must also be a positive number")
    private Double weight;

    @Positive(message = "must also be a positive number")
    private Double height;

    @Size(max = 255, message = "Notes must be 255 characters or less")
    private String notes;

    // Constructors
    public WeighInDTO() {}

    public WeighInDTO(Integer userId,
                      MeasurementSystem measurement,
                      LocalDate date,
                      Double weight,
                      Double height,
                      String notes) {
        this.userId = userId;
        this.measurement = measurement;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public MeasurementSystem getMeasurement() { return measurement; }
    public void setMeasurement(MeasurementSystem measurement) { this.measurement = measurement; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}