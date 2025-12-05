package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Food;
import edu.fscj.cen4940.capstone.repository.FoodRepository;

import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("Food Repository CRUD Tests")
public class FoodApplicationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    private CreateAndPersist createFood;


    @Test
    @DisplayName("Save food and retrieve it by ID")
    public void saveFood_ShouldSaveFood() {
        Food food = createFood.chocolateMilk();

        Food savedFood = foodRepository.save(food);

        Food foundFood = entityManager.find(Food.class, savedFood.getId());
        assertThat(foundFood).isEqualTo(savedFood);
    }

    @Test
    @DisplayName("Find non-existing food returns empty")
    public void findByFood_ShouldReturnEmpty_WhenFoodDoesNotExist() {
        Integer nonExistedId = 1000;
        Optional<Food> foundFood = foodRepository.findById(nonExistedId);
        assertThat(foundFood).isEmpty();
    }

    @Test
    @DisplayName("Find by name ignore case")
    void findByNameIgnoreCase_ShouldReturnFood() {
        Food food = createFood.chocolateMilk();

        Optional<Food> result = foodRepository.findByNameIgnoreCase("chocolate milk");
        assertThat(result).isPresent().contains(food);
    }

    @Test
    @DisplayName("Check if food exists by name ignore case")
    void existsByNameIgnoreCase_ShouldReturnTrue() {
        Food food = createFood.chocolateMilk();
        foodRepository.save(food);

        boolean exists = foodRepository.existsByNameIgnoreCase("chocolate milk");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Find foods by calories range")
    void findAllByCaloriesBetween_ShouldReturnMatchingFoods() {
        Food food1 = createFood.oatmeal();
        Food food2 = createFood.eggs();
        Food food3 = createFood.steak();
        foodRepository.save(food1);
        foodRepository.save(food2);
        foodRepository.save(food3);

        List<Food> results = foodRepository.findAllByCaloriesBetween(100.0, 200.0);
        assertThat(results).containsExactly(food1);
    }

    @Test
    @DisplayName("Find foods with calories <= max")
    void findByCaloriesIsLessThanEqual_ShouldReturnMatchingFoods() {
        Food food = createFood.yogurt();
        foodRepository.save(food);

        List<Food> results = foodRepository.findByCaloriesIsLessThanEqual(150.0);
        assertThat(results).contains(food);
    }

    @Test
    @DisplayName("Find foods with calories >= min")
    void findByCaloriesIsGreaterThanEqual_ShouldReturnMatchingFoods() {
        Food food = createFood.burger();
        foodRepository.save(food);

        List<Food> results = foodRepository.findByCaloriesIsGreaterThanEqual(400.0);
        assertThat(results).contains(food);
    }

    @Test
    @DisplayName("Find foods by partial name (autocomplete)")
    void findByNameContainingIgnoreCase_ShouldReturnMatchingFoods() {
        Food food = createFood.chocolateMilk();

        foodRepository.save(food);

        List<Food> results = foodRepository.findByNameContainingIgnoreCase("choco");
        assertThat(results).contains(food);;
    }
}
