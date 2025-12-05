package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findAll();
    List<Exercise> findByName(String name);
    void deleteById(Integer id);
    List<Exercise> findByType(ExerciseType type);
    boolean existsById(Integer id);
}
