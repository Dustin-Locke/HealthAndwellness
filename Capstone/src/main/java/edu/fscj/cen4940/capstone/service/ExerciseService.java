package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.ExerciseDTO;
import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import edu.fscj.cen4940.capstone.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    private ExerciseDTO convertToDTO(Exercise exercise) {
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getType(),
                exercise.getName()
        );
    }

    private Exercise convertToEntity(ExerciseDTO dto) {
        return new Exercise(
                dto.getType(),
                dto.getName()
        );
    }

    public ExerciseDTO save(ExerciseDTO exerciseDTO) {
        Exercise exercise = convertToEntity(exerciseDTO);
        Exercise saved = exerciseRepository.save(exercise);
        return convertToDTO(saved);
    }

    public List<ExerciseDTO> findAll() {
        return exerciseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ExerciseDTO> findById(Integer id) {
        return exerciseRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ExerciseDTO> findByType(ExerciseType type) {
        return exerciseRepository.findByType(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> findByName(String name) {
        return exerciseRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    public Optional<ExerciseDTO> updateExercise(Integer id, ExerciseDTO exerciseDTO) {
        return exerciseRepository.findById(id).map(exercise -> {
            exercise.setName(exerciseDTO.getName());
            exercise.setType(exerciseDTO.getType());
            Exercise updated = exerciseRepository.save(exercise);
            return convertToDTO(updated);
        });
    }

    public boolean existsById(Integer id) {
        return exerciseRepository.existsById(id);
    }


    public void deleteById(Integer id) {
        exerciseRepository.deleteById(id);
    }

}
