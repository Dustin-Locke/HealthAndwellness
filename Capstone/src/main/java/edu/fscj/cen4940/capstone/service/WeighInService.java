package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.WeighInDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.entity.WeighIn;
import edu.fscj.cen4940.capstone.repository.WeighInRepository;
import edu.fscj.cen4940.capstone.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeighInService {

    private WeighInRepository weighInRepository;
    private UserRepository userRepository;

    @Autowired
    public WeighInService(WeighInRepository weighInRepository, UserRepository userRepository) {
        this.weighInRepository = weighInRepository;
        this.userRepository = userRepository;
    }

    // get all weigh in measurements
    public List<WeighIn> getAllWeighIns() {
        return weighInRepository.findAll();
    }

    // get weigh-ins by user ID
    public List<WeighIn> getWeighInsByUserId(Integer userId) {
        User user = new User();
        user.setId(userId);
        return weighInRepository.findByUser(user);
    }

    // Optional weigh in by ID
    public Optional<WeighIn> getWeighInById(Integer id) {
        return weighInRepository.findById(id);
    }

    // Create or update weigh in
    public WeighIn saveWeighIn(WeighIn weighIn) {
        WeighIn saved = weighInRepository.save(weighIn);

        // Update the user's current weight ONLY if this is the most recent weigh-in by date
        if (saved.getUser() != null && saved.getWeight() != null && saved.getDate() != null) {
            // Fetch the full User entity to avoid lazy loading issues
            Integer userId = saved.getUser().getId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Get all weigh-ins for this user and find the one with the most recent date
                List<WeighIn> allWeighIns = weighInRepository.findByUser(user);
                Optional<WeighIn> mostRecentWeighIn = allWeighIns.stream()
                        .max((w1, w2) -> w1.getDate().compareTo(w2.getDate()));

                // Only update user's weight if this saved weigh-in is the most recent one by date
                if (mostRecentWeighIn.isPresent() &&
                        mostRecentWeighIn.get().getWeighInId().equals(saved.getWeighInId())) {
                    user.setWeight(saved.getWeight());
                    userRepository.save(user);
                    System.out.println("✅ Updated user weight to: " + saved.getWeight() + " (most recent weigh-in)");
                } else {
                    System.out.println("ℹ️ Saved historical weigh-in, current weight unchanged");
                }
            }
        }

        return saved;
    }

    // Delete a weighIn by Id
    public void deleteWeighIn(Integer id) {
        weighInRepository.deleteById(id);
    }

    // Filter by date
    public List<WeighIn> getWeighInsByDate(LocalDate date) {
        return weighInRepository.findByDate(date);
    }

    // Filter by weight range
    public List<WeighIn> getWeighInsByWeightRange(Double min, Double max) {
        return weighInRepository.findByWeightBetween(min, max);
    }

    // Add more code later
}