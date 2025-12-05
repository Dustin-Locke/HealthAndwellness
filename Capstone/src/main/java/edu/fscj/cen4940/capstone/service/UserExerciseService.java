package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.UserExerciseDTO;
import edu.fscj.cen4940.capstone.entity.Exercise;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.entity.UserExercise;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import edu.fscj.cen4940.capstone.enums.ExerciseIntensity;
import edu.fscj.cen4940.capstone.repository.UserExerciseRepository;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import edu.fscj.cen4940.capstone.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserExerciseService {

    @Autowired
    private UserExerciseRepository userExerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private UserExerciseDTO convertToDTO(UserExercise userExercise) {
        return new UserExerciseDTO(
                userExercise.getId(),
                userExercise.getUser().getId(),
                userExercise.getExercise().getId(),
                userExercise.getExercise().getName(),
                userExercise.getDate(),
                userExercise.getDurationMinutes(),
                userExercise.getReps(),
                userExercise.getSets(),
                userExercise.getIntensity(),
                userExercise.getCaloriesBurned(),
                userExercise.getComplete()
        );
    }

    private UserExercise convertToEntity(UserExerciseDTO dto) {
        UserExercise ue = new UserExercise();

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
            ue.setUser(user);
        } else {
            throw new RuntimeException("User ID is required");
        }

        if (dto.getExerciseId() != null) {
            Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + dto.getExerciseId()));
            ue.setExercise(exercise);
        } else {
            throw new RuntimeException("Exercise ID is required");
        }

        ue.setDate(dto.getDate());
        ue.setDurationMinutes(dto.getDurationMinutes());
        ue.setReps(dto.getReps());
        ue.setSets(dto.getSets());
        ue.setIntensity(dto.getIntensity());
        ue.setComplete(dto.getComplete());

        // Calculate calories with the full user and exercise objects
        ue.setCaloriesBurned(calculateCaloriesBurned(ue.getUser(), ue.getExercise(), dto));

        return ue;
    }

    private Double calculateCaloriesBurned(User user, Exercise exercise, UserExerciseDTO dto) {
        ExerciseType type = exercise.getType();
        ExerciseIntensity intensity = dto.getIntensity();

        double baseMet = type.getMetValue();

        // Handle null weight - use initialWeight as fallback, or default to 70kg (154 lbs)
        Double userWeightLbs = user.getWeight();
        if (userWeightLbs == null || userWeightLbs == 0) {
            userWeightLbs = user.getInitialWeight();
        }
        if (userWeightLbs == null || userWeightLbs == 0) {
            userWeightLbs = 154.0; // Default to 154 lbs (~70kg) if no weight available
        }

        // CRITICAL: Convert pounds to kilograms for MET formula (1 lb = 0.453592 kg)
        double weightKg = userWeightLbs * 0.453592;

        double adjustedMet = 0.0;
        if (type == ExerciseType.AEROBIC || type == ExerciseType.ANAEROBIC) {
            adjustedMet = baseMet * intensity.getMultiplier(type);
        } else {
            adjustedMet = baseMet;
        }

        double calories = 0.0;

        // Duration-based calories (for cardio, yoga, etc.)
        // Formula: (MET * 3.5 * weight_kg / 200) * duration_minutes
        if (dto.getDurationMinutes() != null && dto.getDurationMinutes() > 0) {
            calories += (adjustedMet * 3.5 * weightKg / 200) * dto.getDurationMinutes();
        }

        // Reps/sets-based calories (for strength training)
        if (dto.getReps() != null && dto.getSets() != null) {
            double minutesPerRep = 0.5; // average time per rep in minutes, adjust as needed
            double totalMinutes = dto.getReps() * dto.getSets() * minutesPerRep;

            calories += (adjustedMet * 3.5 * weightKg / 200) * totalMinutes;
        }

        return calories;
    }

    public UserExerciseDTO save(UserExerciseDTO dto) {
        UserExercise entity = convertToEntity(dto);
        UserExercise saved = userExerciseRepository.save(entity);
        return convertToDTO(saved);
    }

    public List<UserExerciseDTO> getAll() {
        return userExerciseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserExerciseDTO> getById(Integer id) {
        return userExerciseRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<UserExerciseDTO> getByUser(User user) {
        return userExerciseRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserExerciseDTO> getByUserAndDate(User user, LocalDate date) {
        return userExerciseRepository.findByUserAndDate(user, date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserExerciseDTO> getByUserAndExercise(User user, Exercise exercise) {
        return userExerciseRepository.findByUserAndExercise(user, exercise)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserExerciseDTO> getByUserAndComplete(User user, Boolean complete) {
        return userExerciseRepository.findByUserAndComplete(user, complete)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserExerciseDTO> getByUserAndDateRange(User user, LocalDate start, LocalDate end) {
        return userExerciseRepository.findByUserAndDateBetween(user, start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public Optional<UserExerciseDTO> update(Integer id, UserExerciseDTO dto) {
        return userExerciseRepository.findById(id).map(existing -> {
            if (dto.getExerciseId() != null) {
                Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + dto.getExerciseId()));
                existing.setExercise(exercise);
            }

            if (dto.getDate() != null) {
                existing.setDate(dto.getDate());
            }
            if (dto.getDurationMinutes() != null) {
                existing.setDurationMinutes(dto.getDurationMinutes());
            }
            if (dto.getReps() != null) {
                existing.setReps(dto.getReps());
            }
            if (dto.getSets() != null) {
                existing.setSets(dto.getSets());
            }
            if (dto.getComplete() != null) {
                existing.setComplete(dto.getComplete());
            }
            if (dto.getIntensity() != null) {
                existing.setIntensity(dto.getIntensity());
            }

            existing.setCaloriesBurned(
                    calculateCaloriesBurned(existing.getUser(), existing.getExercise(), dto)
            );

            UserExercise updated = userExerciseRepository.save(existing);
            return convertToDTO(updated);
        });
    }

    public void delete(Integer id) {
        userExerciseRepository.deleteById(id);
    }
}