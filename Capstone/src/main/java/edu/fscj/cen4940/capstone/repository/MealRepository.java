package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.Meal;
import edu.fscj.cen4940.capstone.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    // Find meals by user
    List<Meal> findByUserId(Integer userId);

    // Find meals by user and date
    List<Meal> findByUserIdAndDate(Integer userId, LocalDate date);

    // Find meals by user and meal type
    List<Meal> findByUserIdAndType(Integer userId, MealType mealType);

    // Finds by user, meal type, and date
    List<Meal> findByUserIdAndTypeAndDate(Integer userId, MealType mealType, LocalDate date);
}
