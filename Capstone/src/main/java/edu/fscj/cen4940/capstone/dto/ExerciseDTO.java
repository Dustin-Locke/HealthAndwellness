package edu.fscj.cen4940.capstone.dto;

import edu.fscj.cen4940.capstone.enums.ExerciseIntensity;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExerciseDTO {

    private Integer id;
    @NotNull private ExerciseType type;
    @NotBlank private String name;

    public ExerciseDTO() {}

    public ExerciseDTO(ExerciseType type,
                       String name) {
        this.type = type;
        this.name = name;
    }

    public ExerciseDTO(Integer id,
                       ExerciseType type,
                       String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ExerciseType getType() { return type; }
    public void setType(ExerciseType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
