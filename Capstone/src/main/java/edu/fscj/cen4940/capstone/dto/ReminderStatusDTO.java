package edu.fscj.cen4940.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class ReminderStatusDTO {

    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private LocalDate lastNotified;
    @Getter
    @Setter
    private String lastNotifiedPeriod; // e.g., "DAILY", "WEEKLY", etc.

}

