package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.ExerciseIntensity;
import java.time.LocalDate;

public class UserExerciseDTO {

    private Integer id;
    private Integer userId;
    private Integer exerciseId;
    private String exerciseName;
    private LocalDate date;
    private Double durationMinutes;
    private Integer reps;
    private Integer sets;
    private ExerciseIntensity intensity;
    private Double caloriesBurned;
    private Boolean complete;

    // Default constructor
    public UserExerciseDTO() {}

    // Full constructor
    public UserExerciseDTO(Integer id,
                           Integer userId,
                           Integer exerciseId,
                           String exerciseName,
                           LocalDate date,
                           Double durationMinutes,
                           Integer reps,
                           Integer sets,
                           ExerciseIntensity intensity,
                           Double caloriesBurned,
                           Boolean complete) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.reps = reps;
        this.sets = sets;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
        this.complete = complete;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getExerciseId() { return exerciseId; }
    public void setExerciseId(Integer exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public ExerciseIntensity getIntensity() { return intensity; }
    public void setIntensity(ExerciseIntensity intensity) {
        this.intensity = intensity;
    }

    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Boolean getComplete() { return complete; }
    public void setComplete(Boolean complete) { this.complete = complete; }
}