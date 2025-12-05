package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.UserExercise;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {

    List<UserExercise> findByUser(User user);

    List<UserExercise> findByUserAndDate(User user, LocalDate date);

    List<UserExercise> findByUserAndExercise(User user, Exercise exercise);

    List<UserExercise> findByUserAndComplete(User user, Boolean complete);

    List<UserExercise> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
