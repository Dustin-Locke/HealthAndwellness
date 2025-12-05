package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Food;
import edu.fscj.cen4940.capstone.entity.Meal;
import edu.fscj.cen4940.capstone.entity.MealFood;
import edu.fscj.cen4940.capstone.enums.MealType;
import edu.fscj.cen4940.capstone.repository.FoodRepository;
import edu.fscj.cen4940.capstone.repository.MealFoodRepository;
import edu.fscj.cen4940.capstone.repository.MealRepository;

import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("MealFood Repository CRUD Tests")
public class MealFoodApplicationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealFoodRepository mealFoodRepository;

    @Autowired
    private CreateAndPersist createMealFood;

    @Test
    @DisplayName("Save MealFood should persist it correctly")
    public void saveMealFood_ShouldSaveMealFood() {
        MealFood savedMealFood = createMealFood.yogurtForBreakfast(2.0);

        Meal savedMeal = savedMealFood.getMeal();
        Food savedFood = savedMealFood.getFood();

        MealFood foundMealFood = entityManager.find(MealFood.class, savedMealFood.getId());
        assertThat(foundMealFood).isNotNull();
        assertThat(foundMealFood.getServings()).isEqualTo(2.0);
        assertThat(foundMealFood.getMeal().getMealId()).isEqualTo(savedMeal.getMealId());
        assertThat(foundMealFood.getFood().getId()).isEqualTo(savedFood.getId());
    }

    @Test
    @DisplayName("Find by non-existing MealFood ID should return empty")
    public void findByMealFood_ShouldReturnEmpty_WhenMealFoodDoesNotExist() {
        Integer nonExistentId = 9999;
        Optional<MealFood> foundMealFood = mealFoodRepository.findById(nonExistentId);
        assertThat(foundMealFood).isEmpty();
    }

    @Test
    @DisplayName("Find by meal ID should return all MealFood for that meal")
    public void findByMealId_ShouldReturnMealFoodsForMeal() {
        MealFood savedMealFood = createMealFood.steakForDinner(1.5);
        Meal savedMeal = savedMealFood.getMeal();

        List<MealFood> mealFoods = mealFoodRepository.findByMealId(savedMeal.getMealId());
        assertThat(mealFoods).hasSize(1);
        assertThat(mealFoods.get(0).getMeal().getMealId()).isEqualTo(savedMeal.getMealId());
    }

    @Test
    @DisplayName("Find by food ID should return all MealFood for that food")
    public void findByFoodId_ShouldReturnMealFoodsForFood() {
        MealFood mealFood = createMealFood.yogurtForBreakfast(0.5);

        Food savedFood = mealFood.getFood();

        List<MealFood> mealFoods = mealFoodRepository.findByFoodId(savedFood.getId());
        assertThat(mealFoods).hasSize(1);
        assertThat(mealFoods.get(0).getFood().getId()).isEqualTo(savedFood.getId());
    }

    @Test
    @DisplayName("Find by meal ID and food ID should return correct MealFood")
    public void findByMealIdAndFoodId_ShouldReturnCorrectMealFood() {
        MealFood savedMealFood = createMealFood.steakForDinner(4.0);
        Meal savedMeal = savedMealFood.getMeal();
        Food savedFood = savedMealFood.getFood();

        List<MealFood> mealFoods = mealFoodRepository.findByMealIdAndFoodId(
                savedMeal.getMealId(), savedFood.getId()
        );
        assertThat(mealFoods).hasSize(1);
        assertThat(mealFoods.get(0).getMeal().getMealId()).isEqualTo(savedMeal.getMealId());
        assertThat(mealFoods.get(0).getFood().getId()).isEqualTo(savedFood.getId());
    }
}
