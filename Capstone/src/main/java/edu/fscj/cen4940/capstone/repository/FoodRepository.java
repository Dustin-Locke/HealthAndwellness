package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.dto.FoodDTO;
import edu.fscj.cen4940.capstone.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Integer> {

    // find food by name (case-insensitive)
    Optional<Food> findByNameIgnoreCase(String name);

    // list foods that match a name pattern (useful for autocompletion)
    List<Food> findByNameContainingIgnoreCase(String partialName);

    List<Food> findAllByCaloriesBetween(Double minCalories, Double maxCalories);

    List<Food> findByCaloriesIsLessThanEqual(Double maxCalories);

    List<Food> findByCaloriesIsGreaterThanEqual(Double minCalories);

    // exists check for duplicates
    boolean existsByNameIgnoreCase(String name);
}
