package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.UserExerciseDTO;
import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.service.UserExerciseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-exercises")
public class UserExerciseController {

    @Autowired
    private UserExerciseService userExerciseService;

    @PostMapping
    public ResponseEntity<UserExerciseDTO> create(@RequestBody @Valid UserExerciseDTO dto) {
        UserExerciseDTO saved = userExerciseService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<UserExerciseDTO>> getAll() {
        List<UserExerciseDTO> exercises = userExerciseService.getAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserExerciseDTO> getById(@PathVariable Integer id) {
        Optional<UserExerciseDTO> exercise = userExerciseService.getById(id);
        return exercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserExerciseDTO>> getByUser(@PathVariable Integer userId) {
        User user = new User();
        user.setId(userId);
        List<UserExerciseDTO> exercises = userExerciseService.getByUser(user);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<UserExerciseDTO>> getByUserAndDate(@PathVariable Integer userId,
                                                                  @PathVariable String date) {
        User user = new User();
        user.setId(userId);
        LocalDate localDate = LocalDate.parse(date);
        List<UserExerciseDTO> exercises = userExerciseService.getByUserAndDate(user, localDate);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/exercise/{exerciseId}")
    public ResponseEntity<List<UserExerciseDTO>> getByUserAndExercise(@PathVariable Integer userId,
                                                                      @PathVariable Integer exerciseId) {
        User user = new User();
        user.setId(userId);
        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        List<UserExerciseDTO> exercises = userExerciseService.getByUserAndExercise(user, exercise);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/complete/{status}")
    public ResponseEntity<List<UserExerciseDTO>> getByUserAndComplete(@PathVariable Integer userId,
                                                                      @PathVariable Boolean status) {
        User user = new User();
        user.setId(userId);
        List<UserExerciseDTO> exercises = userExerciseService.getByUserAndComplete(user, status);
        return ResponseEntity.ok(exercises);
    }

    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<UserExerciseDTO>> getByUserAndDateRange(@PathVariable Integer userId,
                                                                       @RequestParam String start,
                                                                       @RequestParam String end) {
        User user = new User();
        user.setId(userId);
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<UserExerciseDTO> exercises = userExerciseService.getByUserAndDateRange(user, startDate, endDate);
        return ResponseEntity.ok(exercises);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserExerciseDTO> update(@PathVariable Integer id,
                                                  @RequestBody @Valid UserExerciseDTO dto) {
        Optional<UserExerciseDTO> updated = userExerciseService.update(id, dto);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            userExerciseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
