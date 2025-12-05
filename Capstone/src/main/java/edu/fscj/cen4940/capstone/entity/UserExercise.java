package edu.fscj.cen4940.capstone.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import edu.fscj.cen4940.capstone.enums.ExerciseIntensity;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_exercises")
public class UserExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userExerciseId;

    // Foreign key to User
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Foreign key to Exercise
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    // Duration in minutes
    @Column(name="duration_minutes")
    private Double durationMinutes;

    // Optional: number of reps/sets if relevant
    @Column
    private Integer reps;

    @Column
    private Integer sets;

    @Enumerated(EnumType.STRING)
    @Column
    private ExerciseIntensity intensity;

    // Optional: actual calories burned (calculated server-side)
    @Column(name="calories_burned")
    private Double caloriesBurned;

    @NotNull
    @Column(nullable = false)
    private Boolean complete;

    public UserExercise() {}

    public UserExercise(User user,
                        Exercise exercise,
                        LocalDate date,
                        Double durationMinutes,
                        ExerciseIntensity intensity,
                        Boolean complete) {
        this.user = user;
        this.exercise = exercise;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.complete = complete;
    }

    public UserExercise(User user,
                        Exercise exercise,
                        LocalDate date,
                        Integer reps,
                        Integer sets,
                        ExerciseIntensity intensity,
                        Boolean complete) {
        this.user = user;
        this.exercise = exercise;
        this.date = date;
        this.reps = reps;
        this.sets = sets;
        this.intensity = intensity;
        this.complete = complete;
    }

    public UserExercise(User user,
                        Exercise exercise,
                        LocalDate date,
                        Double durationMinutes,
                        Integer sets,
                        ExerciseIntensity intensity,
                        Boolean complete) {
        this.user = user;
        this.exercise = exercise;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.sets = sets;
        this.intensity = intensity;
        this.complete = complete;
    }

    public UserExercise(User user,
                        Exercise exercise,
                        LocalDate date,
                        Double durationMinutes,
                        Integer reps,
                        Integer sets,
                        ExerciseIntensity intensity,
                        Boolean complete) {
        this.user = user;
        this.exercise = exercise;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.reps = reps;
        this.sets = sets;
        this.intensity = intensity;
        this.complete = complete;
    }

    public Integer getId() { return userExerciseId; }
    public void setUserId(Integer userExerciseId) { this.userExerciseId = userExerciseId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public ExerciseIntensity getIntensity() { return intensity; }

    public void setIntensity(ExerciseIntensity intensity) {
        this.intensity = intensity;
    }

    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public Boolean getComplete() { return complete; }
    public void setComplete(Boolean complete) { this.complete = complete; }
}
