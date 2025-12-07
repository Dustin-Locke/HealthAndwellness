package edu.fscj.cen4940.capstone.entity;

import edu.fscj.cen4940.capstone.enums.ReminderFrequency;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import edu.fscj.cen4940.capstone.enums.ReminderFrequency;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Entity
@Table(name = "reminder")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false)
    private ReminderType type;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_frequency")
    private ReminderFrequency frequency;

    @NotNull
    @Column(name = "notify_time", nullable = false)
    private LocalTime notifyTime;

    @Column(name = "notify_date")
    private LocalDate notifyDate;

    @Column
    private LocalDate lastNotified;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "last_notified_period")
    private String lastNotifiedPeriod;

    public Reminder() {}

    public Reminder(User user,
                    ReminderType type,
                    Boolean enabled,
                    ReminderFrequency frequency,
                    LocalTime notifyTime,
                    LocalDate notifyDate,
                    LocalDate lastNotified,
                    String title,
                    String message) {
        this.user = user;
        this.type = type;
        this.enabled = enabled;
        this.frequency = frequency;
        this.notifyTime = notifyTime;
        this.notifyDate = notifyDate;
        this.lastNotified = lastNotified;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ReminderType getType() { return type; }
    public void setType(ReminderType type) {
        // Check if current message matches any default
        boolean isDefaultMessage = Arrays.stream(ReminderType.values())
                .anyMatch(rt -> rt.getDefaultMessage().equals(this.message));
        this.type = type;
        // If it was a default, update it to match the new type
        if (isDefaultMessage) {
            this.message = type.getDefaultMessage();
        }
    }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public ReminderFrequency getFrequency() { return frequency; }
    public void setFrequency(ReminderFrequency frequency) { this.frequency = frequency; }

    public LocalTime getNotifyTime() { return notifyTime; }
    public void setNotifyTime(LocalTime notifyTime) { this.notifyTime = notifyTime; }

    public LocalDate getNotifyDate() { return notifyDate; }
    public void setNotifyDate(LocalDate notifyDate) {
        this.notifyDate = notifyDate;
    }

    public LocalDate getLastNotified() { return lastNotified; }
    public void setLastNotified(LocalDate lastNotified) {
        this.lastNotified = lastNotified;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        if (message == null || message.isBlank()) {
            this.message = this.type.getDefaultMessage();
        } else {
            this.message = message;
        }
    }

    public String getLastNotifiedPeriod() { return lastNotifiedPeriod; }
    public void setLastNotifiedPeriod(String lastNotifiedPeriod) {
        this.lastNotifiedPeriod = lastNotifiedPeriod;
    }
}