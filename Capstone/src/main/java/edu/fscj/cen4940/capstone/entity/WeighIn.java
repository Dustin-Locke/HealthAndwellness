package edu.fscj.cen4940.capstone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="WeighIn")
public class WeighIn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer weighInId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Expose userId for JSON without loading entire User object
    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userId;

    @Column(nullable = false)
    private LocalDate date;

    @Column
    private Double height;

    @Column
    private Double weight;

    @Column
    private String notes;

    public WeighIn() {}

    public WeighIn(User user,
                   LocalDate date,
                   Double height,
                   Double weight) {
        this.user = user;
        this.date = date;
        this.height = height;
        this.weight = weight;
        this.notes = null;
    }

    // Getters and setters
    public Integer getWeighInId() { return weighInId; }
    public void setWeighInId(Integer weighInId) { this.weighInId = weighInId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {  this.notes = notes; }
}