package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.MeasurementSystem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UpdateProfileDTO {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotNull private LocalDate dateOfBirth;
    @NotNull private Double height;             // or your own Height type (inches, etc.)
    @NotNull private Double weight;
    private Double goalWeight;
    private MeasurementSystem measurementSystem;  // "IMPERIAL"/"METRIC"

    public UpdateProfileDTO(String firstName, String lastName, LocalDate dateOfBirth, Double height, Double currentWeight, Double goalWeight, MeasurementSystem measurementSystem) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = currentWeight;
        this.goalWeight = goalWeight;
        this.measurementSystem = measurementSystem;
    }

}