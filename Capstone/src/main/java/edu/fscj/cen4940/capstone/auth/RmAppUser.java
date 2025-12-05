package edu.fscj.cen4940.capstone.auth;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "app_users")
public class RmAppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(name = "hash", nullable = false)
    private String passwordHash;

    private Integer heightFeet;
    private Integer heightInches;
    private Double initialWeight;
    private Double goalWeight;

    private LocalDate createdAt = LocalDate.now();

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Integer getHeightFeet() { return heightFeet; }
    public Integer getHeightInches() { return heightInches; }
    public Double getInitialWeight() { return initialWeight; }
    public Double getGoalWeight() { return goalWeight; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setHeightFeet(Integer heightFeet) { this.heightFeet = heightFeet; }
    public void setHeightInches(Integer heightInches) { this.heightInches = heightInches; }
    public void setInitialWeight(Double initialWeight) { this.initialWeight = initialWeight; }
    public void setGoalWeight(Double goalWeight) { this.goalWeight = goalWeight; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
