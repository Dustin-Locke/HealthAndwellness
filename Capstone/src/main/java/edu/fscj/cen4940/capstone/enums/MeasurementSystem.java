package edu.fscj.cen4940.capstone.enums;

public enum MeasurementSystem {
    METRIC(1.0, 1.0),           // kg and cm are the base units
    IMPERIAL(2.205, 0.393701);  // 1 kg = 2.205 lb, 1 cm = 0.393701 in

    private final double weightConversion; // multiply internal kg to display
    private final double heightConversion; // multiply internal cm to display

    MeasurementSystem(double weightConversion, double heightConversion) {
        this.weightConversion = weightConversion;
        this.heightConversion = heightConversion;
    }

    public double toDisplayWeight(double weightKg) {
        return weightKg * weightConversion;
    }

    public double toDisplayHeight(double heightCm) {
        return heightCm * heightConversion;
    }

    public double toInternalWeight(double weight) {
        return weight / weightConversion;
    }

    public double toInternalHeight(double height) {
        return height / heightConversion;
    }
}

