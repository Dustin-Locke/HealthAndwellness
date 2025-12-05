package edu.fscj.cen4940.capstone.util;

import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.entity.*;
import edu.fscj.cen4940.capstone.enums.*;
import edu.fscj.cen4940.capstone.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class CreateAndPersist {

    @Autowired
    EntityManager entityManager;

    @Autowired
    MealRepository mealRepository;

    @Autowired
    MealFoodRepository mealFoodRepository;

    @Autowired
    ReminderRepository reminderRepository;

    @Autowired
    UserExerciseRepository userExerciseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WeighInRepository weighInRepository;

    /** Create USER DTOS */
    public UserDTO userDTO(String email) {
        return new UserDTO(
                "Test",
                "User",
                email + "@example.com",
                LocalDate.of(2000,1,1),
                25,
                180.0,
                180.0,
                70.0,
                180.0,
                "password123");
    }

    /** Create USER */
    public User user(String email) {
        User user;
        User existing = userRepository.findByEmail(email + "@example.com");
        if (existing != null) {
            user = existing;
        } else {
            User newUser = new User();
            newUser.setUsername(email + "@example.com");
            newUser.setFirstName("Test");
            newUser.setLastName("User");
            newUser.setEmail(email + "@example.com");
            newUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
            newUser.setAge(25);
            newUser.setHeight(180.0);
            newUser.setWeight(75.0);
            newUser.setGoalWeight(70.0);
            newUser.setMeasurementSystem(MeasurementSystem.METRIC);
            newUser.setSalt(new byte[16]);
            newUser.setHash(new byte[32]);

            entityManager.persist(newUser);
            entityManager.flush();
            user = newUser;
        }
        return user;
    }

    /** Create EXERCISES */
    public Exercise exercise(ExerciseType exerciseType) {
        Exercise exercise = new Exercise();
        if (exerciseType == ExerciseType.ANAEROBIC) {
            exercise.setName("Pushups");
            exercise.setType(exerciseType);
            entityManager.persist(exercise);
        }
        if (exerciseType == ExerciseType.AEROBIC) {
            exercise.setName("Jogging");
            exercise.setType(ExerciseType.AEROBIC);
            entityManager.persist(exercise);
        }

        return exercise;
    }

    /** Create FOODS */
    public Food food(String name, double calories, double amount, MeasurementUnit unit, double servings) {
        Food food = new Food();
        food.setName(name);
        food.setCalories(calories);
        food.setAmount(amount);
        food.setUnit(unit);
        food.setServings(servings);
        return food;
    }

    public Food persistFood(String name, double calories, double amount, MeasurementUnit unit, double servings) {
        Food food = food(name, calories, amount, unit, servings);
        entityManager.persist(food);
        return food;
    }

    /** Specific foods */
    public Food chocolateMilk() {
        return persistFood("Chocolate Milk", 150.0, 1.0, MeasurementUnit.CUP, 1.0);
    }

    public Food eggs() {
        return persistFood("Eggs", 90.0, 2.0, MeasurementUnit.CUP, 1.0);
    }

    public Food oatmeal() {
        return persistFood("Oatmeal", 150.0, 1.0, MeasurementUnit.CUP, 1.0);
    }

    public Food steak() {
        return persistFood("Steak", 300.00, 4.0, MeasurementUnit.OUNCE_WEIGHT, 1.0);
    }

    public  Food yogurt() {
        return persistFood("Yogurt", 120.0, 6.0, MeasurementUnit.OUNCE_WEIGHT, 1.0);
    }

    public Food burger() {
        return persistFood("Burger", 500.0, 6.0, MeasurementUnit.OUNCE_WEIGHT, 1.0);
    }

    /** Create MEALS */
    public Meal meal(Integer userId, MealType type, LocalDate date) {
        Meal meal = new Meal();
        meal.setUserId(userId);
        meal.setType(type);
        meal.setDate(date);
        return mealRepository.save(meal);
    }

    /** Specific meals */
    public Meal breakfast() {
        return meal(1, MealType.BREAKFAST, LocalDate.now());
    }

    public Meal lunch() {
        return meal(1, MealType.LUNCH, LocalDate.now());
    }

    public Meal dinner(LocalDate date) {
        return meal(1, MealType.DINNER, date);
    }

    public Meal snack(LocalDate date) {
        return meal(1, MealType.SNACK, date);
    }

    /** Create MEAL FOODS */
    public MealFood mealFood(Food food, Meal meal, double servings) {
        MealFood mealFood = new MealFood();
        mealFood.setFood(food);
        mealFood.setMeal(meal);
        mealFood.setServings(servings);
        return mealFoodRepository.save(mealFood);
    }

    /** Specific meal foods */
    public MealFood yogurtForBreakfast(double servings) {
        return mealFood(yogurt(), breakfast(), servings);
    }

    public MealFood steakForDinner(double servings) {
        return mealFood(steak(), dinner(LocalDate.now()), servings);
    }

    /** Create REMINDERS */
    public Reminder reminder(String email, ReminderType type, LocalTime notifyTime, boolean enabled) {
        Reminder r = new Reminder();
        r.setUser(user(email));
        r.setType(type);
        r.setNotifyTime(notifyTime);
        r.setEnabled(enabled);
        r.setTitle("title");
        r.setMessage(type.getDefaultMessage());
        return reminderRepository.save(r);
    }

    /** Specific reminders */
    public Reminder workoutReminder(String email, LocalTime time, Boolean enabled) {
        return reminder(email, ReminderType.WORKOUT, time, enabled);
    }

    public Reminder mealLogReminder(String email, LocalTime time, Boolean enabled) {
        return reminder(email, ReminderType.MEAL_LOG, time, enabled);
    }

    public UserExercise userExercise(
            String email,
            ExerciseType exerciseType,
            LocalDate date) {
        UserExercise userExercise = new UserExercise();
        userExercise.setUser(user(email));
        userExercise.setExercise(exercise(exerciseType));
        userExercise.setDate(date);
        userExercise.setComplete(true);
        return userExerciseRepository.save(userExercise);
    }

    public UserExercise userPushUps(String email) {
        return userExercise(email, ExerciseType.ANAEROBIC, LocalDate.now());
    }

    public UserExercise userJogging(String email) {
        return userExercise(email, ExerciseType.AEROBIC, LocalDate.now());
    }

    public UserExercise userSquats(String email, LocalDate date) {
        return userExercise(email, ExerciseType.ANAEROBIC, date);
    }

    public WeighIn weighIn(String email, double height, double weight) {
        User user = user(email);

        WeighIn weighIn = new WeighIn();
        weighIn.setUser(user);
        weighIn.setDate(LocalDate.now());
        weighIn.setHeight(height);
        weighIn.setWeight(weight);

        return weighInRepository.save(weighIn);
    }

}
