package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.PasswordUpdateDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // @CrossOrigin(origins = {"*"})

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream().map(userService::convertToDTO).collect(Collectors.toList());
        logger.info("Retrieved all users, count={}", userDTOs.size());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            return ResponseEntity.notFound().build();
        }
        UserDTO userDTO = userService.convertToDTO(user);
        logger.info("Found user {}", username);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        Profiler profiler = new Profiler("createUser");
        profiler.start("Create User");

        UserDTO savedUser = userService.create(userDTO);
        logger.info("A new user has been added: {}", savedUser.getUsername());

        TimeInstrument ti = profiler.stop();
        ti.print();

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PreAuthorize("#username == authentication.principal.user.name")
    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username,
                                              @RequestBody @Valid UserDTO userDTO) {

        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("Update user failed, not found: {}", username);
            return ResponseEntity.notFound().build();
        }

        UserDTO updated = userService.update(user.getId(), userDTO);
        logger.info("User updated: {}", username);

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // authenticated username

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userService.updatePassword(user.getId(), dto.getNewPassword());
        logger.info("Password updated for user: {}", username);

        return ResponseEntity.ok("Password updated successfully.");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        if (userService.findByUsername(username) == null) {
            logger.error("Delete user failed, not found: {}", username);
            return ResponseEntity.notFound().build();
        }
        userService.deleteByUsername(username);
        logger.info("Deleted user {}", username);
        return ResponseEntity.noContent().build();
    }
}