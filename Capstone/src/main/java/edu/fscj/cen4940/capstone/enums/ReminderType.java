package edu.fscj.cen4940.capstone.enums;

public enum ReminderType {
    WORKOUT("Time to exercise!"),
    REST_DAY("Enjoy your rest day."),
    DRINK_WATER("Time to hydrate!"),
    WEIGH_IN("Track your weight today."),
    MEAL_LOG("Log your meals."),
    BEDTIME("Time to wind down for bed.");

    private final String defaultMessage;

    ReminderType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
