package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealFoodRepository extends JpaRepository<MealFood, Integer> {

    List<MealFood> findByMealId(Integer mealId);

    List<MealFood> findByFoodId(Integer foodId);

    List<MealFood> findByMealIdAndFoodId(Integer mealId, Integer foodId);
}
