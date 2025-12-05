package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.dto.*;
import edu.fscj.cen4940.capstone.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // PBKDF2 configuration
    private static final int SALT_LENGTH = 16; // 128 bits
    private static final int HASH_ITERATIONS = 10000;
    private static final int HASH_KEY_LENGTH = 256; // 256 bits

    public List<User> findAll() { return userRepository.findAll(); }
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public User findByUsername(String username) {return userRepository.findByUsername(username);}

    @Transactional
    public void deleteByUsername(String username) {userRepository.deleteByUsername(username);}

    public User save( User user ) { return userRepository.save(user); }

    public UserDTO create(UserDTO userDTO) {
        // Convert UserDTO to User entity
        User user = new User();
        if (userDTO.getUsername() == null) {
            user.setUsername(userDTO.getEmail().toLowerCase());
        } else {
            user.setUsername(userDTO.getUsername().toLowerCase());
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setInitialWeight(userDTO.getWeight());
        user.setWeight(userDTO.getWeight());
        user.setGoalWeight(userDTO.getGoalWeight());
        user.setHeight(userDTO.getHeight());

        if (userDTO.getDateOfBirth() != null) {
            LocalDate today = LocalDate.now();
            int age = Period.between(userDTO.getDateOfBirth(), today).getYears();
            user.setAge(age);
        }

        // Generate salt and hash the password
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(userDTO.getPassword(), salt);

        // Set salt and hashed password in User entity
        user.setSalt(salt);
        user.setHash(hashedPassword);

        // Save the User entity
        User savedUser = userRepository.save(user);

        // Convert saved User entity back to UserDTO and return
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO update(Integer id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update editable fields
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setDateOfBirth(userDTO.getDateOfBirth());
        existingUser.setAge(userDTO.getAge());
        existingUser.setWeight(userDTO.getWeight());
        existingUser.setGoalWeight(userDTO.getGoalWeight());
        existingUser.setHeight(userDTO.getHeight());
        existingUser.setMeasurementSystem(userDTO.getMeasurementSystem());

        User savedUser = userRepository.save(existingUser);
        return convertToDTO(savedUser);
    }


    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getAge(),
                user.getInitialWeight(),
                user.getWeight(),
                user.getGoalWeight(),
                user.getHeight(),
                user.getMeasurementSystem());
    }

    // Password hashing logic
    public byte[] hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    HASH_ITERATIONS,
                    HASH_KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return hash; // Store hash as Base64 encoded string
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error during password hashing", e);
        }
    }

    // Generate salt
    public byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt; // Store salt as Base64 encoded string
    }

    public boolean verifyPassword(String password, byte[] storedHash, byte[] storedSalt) {
        byte[] computedHash = hashPassword(password, storedSalt);
        return MessageDigest.isEqual(computedHash, storedHash);
    }

    @Transactional
    public void updatePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        byte[] newSalt = generateSalt();
        byte[] newHash = hashPassword(newPassword, newSalt);

        user.setSalt(newSalt);
        user.setHash(newHash);

        userRepository.save(user);
    }

    public User searchByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

