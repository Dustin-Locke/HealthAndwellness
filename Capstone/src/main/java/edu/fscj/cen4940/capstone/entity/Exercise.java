package edu.fscj.cen4940.capstone.entity;

import edu.fscj.cen4940.capstone.enums.ExerciseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

    @NotBlank
    @Column(nullable = false)
    private String name;

    public Exercise() {}

    public Exercise(ExerciseType type,
                    String name) {
        this.type = type;
        this.name = name;
        // calorie_burn_rate removed in favor of metValue in ExerciseType enum
    }

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public ExerciseType getType() {return type;}
    public void setType(ExerciseType type) {this.type = type;}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

}
