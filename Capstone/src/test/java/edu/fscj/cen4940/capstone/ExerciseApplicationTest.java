package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import edu.fscj.cen4940.capstone.repository.ExerciseRepository;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Import;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(TestHelperConfig.class)
@Transactional
@DisplayName("Exercise Repository Integration Tests")
public class ExerciseApplicationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private CreateAndPersist createAndPersist;


    @Test
    @DisplayName("Save an exercise and verify it was persisted")
    public void saveExercise_ShouldSaveExercise() {
        Exercise exercise = createAndPersist.exercise(ExerciseType.ANAEROBIC);

        Exercise savedExercise = exerciseRepository.save(exercise);

        Exercise foundExercise = entityManager.find(Exercise.class, savedExercise.getId());
        assertThat(foundExercise).isEqualTo(savedExercise);
    }

    @Test
    @DisplayName("Find by non-existent ID should return empty")
    public void findByname_ShouldReturnEmpty_WhenExerciseDoesNotExist() {
        Integer nonExistedId = 1000;
        Optional<Exercise> foundExercise = exerciseRepository.findById(nonExistedId);
        assertThat(foundExercise).isEmpty();
    }

    @Test
    @DisplayName("Find exercise by name should return matching exercise")
    public void findByName_ShouldReturnMatchingExercise() {
        Exercise exercise = createAndPersist.exercise(ExerciseType.ANAEROBIC);

        List<Exercise> exercises = exerciseRepository.findByName("Pushups");
        assertThat(exercises).contains(exercise);
    }

    @Test
    @DisplayName("Find exercises by type should return matching exercises")
    public void findByType_ShouldReturnMatchingExercises() {
        Exercise exercise1 = createAndPersist.exercise(ExerciseType.ANAEROBIC);

        Exercise exercise2 = createAndPersist.exercise(ExerciseType.AEROBIC);

        List<Exercise> anaerobicExercises = exerciseRepository.findByType(ExerciseType.ANAEROBIC);
        assertThat(anaerobicExercises).contains(exercise1);
        assertThat(anaerobicExercises).doesNotContain(exercise2);
    }

    @Test
    @DisplayName("Delete exercise by ID should remove it from the repository")
    public void deleteById_ShouldRemoveExercise() {
        Exercise exercise = createAndPersist.exercise(ExerciseType.ANAEROBIC);

        exerciseRepository.deleteById(exercise.getId());
        Optional<Exercise> deleted = exerciseRepository.findById(exercise.getId());
        assertThat(deleted).isEmpty();
    }


}
