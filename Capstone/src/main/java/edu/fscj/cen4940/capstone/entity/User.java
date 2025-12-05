package edu.fscj.cen4940.capstone.entity;

import edu.fscj.cen4940.capstone.enums.MeasurementSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name="first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name="last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name="date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column()
    private Integer age;

    @Column(name = "initial_weight")
    private Double initialWeight;

    @NotNull
    @Column()
    private Double weight;

    @Column(name = "goal_weight")
    private Double goalWeight;

    @NotNull
    @Column()
    private Double height; // stored in cm

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MeasurementSystem measurementSystem = MeasurementSystem.METRIC;

    @Column(name="email_verified")
    private Boolean emailVerified = false; // user has entered email verification code

    @Column(name = "email_verification_code")
    private String emailVerificationCode;

    @Column(nullable = true)
    private byte[] salt;

    @Column(nullable = true)
    private byte[] hash;

    @Column
    private Integer failedLoginAttempts = 0;
    @Column
    private LocalDateTime accountLockedTime;

    public User() {}

    public User(String username,
                String firstName,
                String lastName,
                String email,
                Integer age,
                LocalDate dateOfBirth,
                Double initialWeight,
                Double weight,
                Double goalWeight,
                Double height,
                MeasurementSystem measurementSystem,
                Boolean emailVerified,
                String emailVerificationCode) {
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
        this.emailVerified = emailVerified;
        this.emailVerificationCode = emailVerificationCode;
    }

    public User(String username,
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

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getInitialWeight() { return initialWeight; }
    public void setInitialWeight(Double initialWeight) { this.initialWeight = initialWeight; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getGoalWeight() { return goalWeight; }
    public void setGoalWeight(Double goalWeight) { this.goalWeight = goalWeight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public MeasurementSystem getMeasurementSystem() {
        return measurementSystem;
    }
    public void setMeasurementSystem(MeasurementSystem measurementSystem) {
        this.measurementSystem = measurementSystem;
    }

    public Boolean isEmailVerified() { return emailVerified; }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationCode() { return emailVerificationCode; }

    public void setEmailVerificationCode(String emailVerificationCode) {
        this.emailVerificationCode = emailVerificationCode;
    }

    public byte[] getSalt() { return salt; }
    public void setSalt(byte[] salt) { this.salt = salt; }

    public byte[] getHash() { return hash; }
    public void setHash(byte[] hash) { this.hash = hash; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getAccountLockedTime() { return accountLockedTime; }
    public void setAccountLockedUntil(LocalDateTime minutes) {
        this.accountLockedTime = minutes;
    }
}
