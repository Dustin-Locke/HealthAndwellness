package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.entity.UserExercise;
import edu.fscj.cen4940.capstone.enums.ExerciseIntensity;
import edu.fscj.cen4940.capstone.repository.UserExerciseRepository;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;


import java.util.List;
import java.util.Optional;
@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("UserExercise Repository Test")
public class UserExerciseApplicationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Autowired
    private CreateAndPersist create;


    @Test
    @DisplayName("Save UserExercise")
    public void saveUserExercise_ShouldSaveUserExercise() {
        UserExercise savedUserExercise = create.userJogging("createUE");

        UserExercise foundUserExercise = entityManager.find(
                UserExercise.class,
                savedUserExercise.getId());
        assertThat(foundUserExercise).isEqualTo(savedUserExercise);
    }

    @Test
    @DisplayName("Delete UserExercise removes the entry")
    void deleteUserExercise_ShouldRemoveEntry() {
        UserExercise savedUserExercise = create.userPushUps("deleteUE");

        userExerciseRepository.delete(savedUserExercise);

        UserExercise foundUserExercise = entityManager.find(
                UserExercise.class,
                savedUserExercise.getId());
        assertThat(foundUserExercise).isNull();
    }

    @Test
    @DisplayName("Empty optional returned when user exercise does not exist")
    void findById_ShouldReturnEmpty_WhenExerciseDoesNotExist() {
        Optional<UserExercise> foundUserExercise =
                userExerciseRepository
                        .findById(9999);
        assertThat(foundUserExercise).isEmpty();
    }

    @Test
    @DisplayName("Return matching exercises for user on exact date")
    void findByUserAndDate_ShouldReturnMatchingExercises() {
        LocalDate date = LocalDate.of(2025, 9, 12);
        UserExercise savedUserExercise = create.userSquats("date", date);
        User user = savedUserExercise.getUser();

        List<UserExercise> foundUserExercises =
                userExerciseRepository
                        .findByUserAndDate(user, savedUserExercise.getDate());
        assertThat(foundUserExercises)
                .containsExactly(savedUserExercise)
                .hasSize(1);
    }

    @Test
    @DisplayName("Return matching exercises for user")
    void findByUserAndExercise_ShouldReturnMatchingExercises() {
        UserExercise savedUserExercise = create.userPushUps("exerciseTest");
        User user = savedUserExercise.getUser();
        Exercise exercise = savedUserExercise.getExercise();

        List<UserExercise> foundUserExercise =
                userExerciseRepository
                        .findByUserAndExercise(user, exercise);
        assertThat(foundUserExercise)
                .containsExactly(savedUserExercise)
                .hasSize(1);
    }

    @Test
    @DisplayName("Return completed exercises for user")
    void findByUserAndComplete_ShouldReturnCompletedExercises() {
        UserExercise savedUserExercise = create.userPushUps("completeTest");
        User user = savedUserExercise.getUser();

        List<UserExercise> foundUserExercise =
                userExerciseRepository
                        .findByUserAndComplete(user, true);
        assertThat(foundUserExercise)
                .containsExactly(savedUserExercise)
                .hasSize(1);
    }

    @Test
    @DisplayName("Return exercises for user in range of dates")
    void findByUserAndDateBetween_ShouldReturnExercisesInRange() {
        LocalDate date1 = LocalDate.of(2025, 9, 12);
        LocalDate date2 = LocalDate.of(2025, 12, 25);
        UserExercise savedUserExercise1 =
                create.userSquats("rangeTest", date1);
        UserExercise savedUserExercise2 =
                create.userSquats("rangeTest", date2);
        User user = savedUserExercise1.getUser();

        LocalDate startDate = date1.minusDays(1);
        LocalDate endDate = date2.minusDays(1);

        List<UserExercise> foundUserExercise =
                userExerciseRepository
                        .findByUserAndDateBetween(user, startDate, endDate);
        assertThat(foundUserExercise)
                .containsExactly(savedUserExercise1)
                .hasSize(1);
    }

    @Test
    @DisplayName("Return empty list when no exercises exist for user between dates")
    void findByUserAndDate_ShouldReturnEmpty_WhenNoExercisesExist() {
        User user = create.user("noExerciseUser");
        LocalDate date = LocalDate.now();

        List<UserExercise> foundUserExercises =
                userExerciseRepository.findByUserAndDate(user, date);

        assertThat(foundUserExercises).isEmpty();
    }

}
