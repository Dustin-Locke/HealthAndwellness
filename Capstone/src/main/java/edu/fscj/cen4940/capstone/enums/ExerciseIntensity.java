package edu.fscj.cen4940.capstone.enums;

public enum ExerciseIntensity {
    // Updated multipliers for more accurate calorie calculations
    // Light: ~75% of base MET (e.g., light jog = 6 MET from base 8)
    // Moderate: 100% of base MET (standard pace)
    // Vigorous: ~125% of base MET (intense effort)
    LIGHT(0.75, 0.85),
    MODERATE(1.0, 1.0),
    VIGOROUS(1.25, 1.2);

    // aerobic multiplier, anaerobic multiplier
    private final double aerobicMultiplier;
    private final double anaerobicMultiplier;

    ExerciseIntensity(double aerobicMultiplier, double anaerobicMultiplier) {
        this.aerobicMultiplier = aerobicMultiplier;
        this.anaerobicMultiplier = anaerobicMultiplier;
    }

    public double getMultiplier(ExerciseType type) {
        return type == ExerciseType.AEROBIC ? aerobicMultiplier : anaerobicMultiplier;
    }
}