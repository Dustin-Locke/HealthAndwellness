package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Meal;
import edu.fscj.cen4940.capstone.enums.MealType;
import edu.fscj.cen4940.capstone.repository.MealRepository;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;



@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("Meal Repository Tests")
public class MealApplicationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private CreateAndPersist createMeal;

    @Test
    @DisplayName("Save Meal")
    void saveMeal_ShouldSaveMeal() {
        Meal savedMeal = createMeal.breakfast();
        Meal foundMeal = entityManager.find(Meal.class, savedMeal.getMealId());

        assertThat(foundMeal).isEqualTo(savedMeal);
        assertThat(foundMeal.getUserId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find Meal by ID returns empty when not exist")
    void findById_ShouldReturnEmpty_WhenMealDoesNotExist() {
        Optional<Meal> meal = mealRepository.findById(9999);
        assertThat(meal).isEmpty();
    }

    @Test
    @DisplayName("Find Meals by User")
    void findByUserId_ShouldReturnMealsForUser() {
        Meal m1 = createMeal.breakfast();
        Meal m2 = createMeal.lunch();

        List<Meal> meals = mealRepository.findByUserId(1);
        assertThat(meals).hasSize(2).containsExactlyInAnyOrder(m1, m2);
    }

    @Test
    @DisplayName("Find Meals by User returns empty list when none exist")
    void findByUserId_ShouldReturnEmpty_WhenNoMealsExist() {
        List<Meal> meals = mealRepository.findByUserId(9999);
        assertThat(meals).isEmpty();
    }

    @Test
    @DisplayName("Find Meals by User and Date")
    void findByUserIdAndDate_ShouldReturnMealsForDate() {
        LocalDate date = LocalDate.of(2025, 11, 10);
        Meal m1 = createMeal.snack(date);
        Meal m2 = createMeal.dinner(date);

        List<Meal> meals = mealRepository.findByUserIdAndDate(1, date);
        assertThat(meals).hasSize(2).containsExactlyInAnyOrder(m1, m2);
    }

    @Test
    @DisplayName("Find Meals by User and Type")
    void findByUserIdAndType_ShouldReturnMealsOfType() {
        Meal m1 = createMeal.breakfast();
        createMeal.lunch();

        List<Meal> meals = mealRepository.findByUserIdAndType(1, MealType.BREAKFAST);
        assertThat(meals).hasSize(1).containsExactly(m1);
    }

    @Test
    @DisplayName("Find Meals by User, Type, and Date")
    void findByUserIdAndTypeAndDate_ShouldReturnMatchingMeal() {
        LocalDate date = LocalDate.of(2025, 11, 10);
        Meal m1 = createMeal.snack(date);
        createMeal.snack(date.minusDays(1));

        List<Meal> meals = mealRepository.findByUserIdAndTypeAndDate(1, MealType.SNACK, date);
        assertThat(meals).hasSize(1).containsExactly(m1);
    }

    @Test
    @DisplayName("Saving invalid Meal should fail validation")
    void saveMeal_InvalidMeal_ShouldFailValidation() {

        Meal invalidMeal = new Meal(); // all fields null

        assertThatThrownBy(() ->
                entityManager.persistAndFlush(invalidMeal)
        )
                .isInstanceOf(PropertyValueException.class);
    }



}
