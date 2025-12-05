package edu.fscj.cen4940.capstone.enums;

public enum ExerciseType {
    AEROBIC(8.0),
    ANAEROBIC(5.0),
    FLEXIBILITY(2.8),
    BALANCE(3.0),
    MIXED(5.0);

    private final double metValue; //Metabolic Equivalent of Task

    ExerciseType(double metValue) {
        this.metValue = metValue;
    }

    public double getMetValue() {
        return metValue;
    }
}
