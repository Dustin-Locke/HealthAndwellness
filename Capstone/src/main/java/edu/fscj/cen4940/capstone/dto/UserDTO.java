package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.MeasurementSystem;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserDTO {

    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    private Integer age;

    private Double initialWeight;
    @NotNull
    @Positive(message = "Weight must be a positive number")
    private Double weight;
    @Positive(message = "Goal weight must be a positive number")
    private Double goalWeight;
    @NotNull
    @Positive(message = "Height must be a positive number")
    private Double height;
    private MeasurementSystem measurementSystem;



    public UserDTO(){}

    public UserDTO(String firstName,
                   String lastName,
                   String email,
                   LocalDate dateOfBirth,
                   Integer age,
                   Double initialWeight,
                   Double weight,
                   Double goalWeight,
                   Double height,
                   String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.initialWeight = initialWeight;
        this.weight = weight;
        this.goalWeight = goalWeight;
        this.height = height;
        this.password = password;
    }

    public UserDTO(String username,
                   String firstName,
                   String lastName,
                   String email,
                   LocalDate dateOfBirth,
                   Integer age,
                   Double initialWeight,
                   Double weight,
                   Double goalWeight,
                   Double height,
                   MeasurementSystem measurementSystem) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.initialWeight = initialWeight;
        this.weight = weight;
        this.goalWeight = goalWeight;
        this.height = height;
        this.measurementSystem = measurementSystem;
    }

    public UserDTO(String username,
                String firstName,
                String lastName,
                String email,
                Integer age,
                LocalDate dateOfBirth,
                Double weight,
                Double goalWeight,
                Double height,
                MeasurementSystem measurementSystem) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.weight = weight;
        this.goalWeight = goalWeight;
        this.height = height;
        this.measurementSystem = measurementSystem;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = email;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getInitialWeight() { return initialWeight; }
    public void setInitialWeight(Double initialWeight) { this.initialWeight = initialWeight; }

    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getGoalWeight() { return goalWeight; }
    public void setGoalWeight(Double goalWeight) { this.goalWeight = goalWeight; }

    public MeasurementSystem getMeasurementSystem() {
        return measurementSystem;
    }
    public void setMeasurementSystem(MeasurementSystem measurementSystem) {
        this.measurementSystem = measurementSystem;
    }
}
