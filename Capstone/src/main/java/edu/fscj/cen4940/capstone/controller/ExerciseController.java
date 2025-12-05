package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.ExerciseDTO;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import edu.fscj.cen4940.capstone.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseDTO> createExercise(@RequestBody @Valid ExerciseDTO exerciseDTO) {
        ExerciseDTO saved = exerciseService.save(exerciseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.findAll();
        return ResponseEntity.ok(exercises); // always 200, empty list if none
    }


    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Integer id) {
        return exerciseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByName(@PathVariable String name) {
        List<ExerciseDTO> results = exerciseService.findByName(name);
        return ResponseEntity.ok(results); // always 200, empty list if none
    }



    @GetMapping("/type/{type}")
    public ResponseEntity<List<ExerciseDTO>> getExerciseByType(@PathVariable ExerciseType type) {
        List<ExerciseDTO> results = exerciseService.findByType(type);
        return ResponseEntity.ok(results); // always 200, empty list if none
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(
            @PathVariable Integer id,
            @RequestBody @Valid ExerciseDTO exerciseDTO) {

        Optional<ExerciseDTO> updated = exerciseService.updateExercise(id, exerciseDTO);

        return updated
                .map(dto -> ResponseEntity.ok(dto)) // return 200 with updated object
                .orElseGet(() -> ResponseEntity.notFound().build()); // return 404 if not found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseById(@PathVariable Integer id) {
        if (!exerciseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        exerciseService.deleteById(id);
        return ResponseEntity.noContent().build();

    }
}
