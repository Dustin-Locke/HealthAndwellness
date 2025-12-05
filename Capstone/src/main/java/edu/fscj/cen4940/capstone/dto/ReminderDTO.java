package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.enums.ReminderFrequency;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.fscj.cen4940.capstone.enums.ReminderFrequency;
import edu.fscj.cen4940.capstone.enums.ReminderType;

public class ReminderDTO {
    private Integer id;
    @NotNull private Integer userId;
    @NotNull private ReminderType type;
    @NotNull private Boolean enabled = true;
    @NotNull private ReminderFrequency frequency;
    @NotNull private LocalTime notifyTime;
    // for one-time notifications
    private LocalDate notifyDate;
    private LocalDate lastNotified;
    @NotBlank String title;
    @NotBlank
    private String message;

    // Message field omitted. It is being handled by the ReminderType enum
    public ReminderDTO() {}

    public ReminderDTO(Integer id,
                    Integer userId,
                    ReminderType type,
                    Boolean enabled,
                    ReminderFrequency frequency,
                    LocalTime notifyTime,
                    LocalDate notifyDate,
                    LocalDate lastNotified,
                    String title) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.enabled = enabled;
        this.frequency = frequency;
        this.notifyTime = notifyTime;
        this.notifyDate = notifyDate;
        this.lastNotified = lastNotified;
        this.title = title;
        this.message = type.getDefaultMessage();
    }

    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUser(Integer userId) { this.userId = userId; }

    public ReminderType getType() { return type; }
    public void setType(ReminderType type) {
        this.type = type;
        this.message = type.getDefaultMessage();
    }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public ReminderFrequency getFrequency() { return frequency; }
    public void setFrequency(ReminderFrequency frequency) { this.frequency = frequency; }

    public LocalTime getNotifyTime() { return notifyTime; }
    public void setNotifyTime(LocalTime notifyTime) { this.notifyTime = notifyTime; }

    public LocalDate getNotifyDate() { return notifyDate; }
    public void setNotifyDate(LocalDate notifyDate) {this.notifyDate = notifyDate; }

    public LocalDate getLastNotified() { return lastNotified; }
    public void setLastNotified(LocalDate lastNotified) { this.lastNotified = lastNotified; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
