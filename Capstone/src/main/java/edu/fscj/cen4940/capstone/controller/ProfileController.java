package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.UpdateProfileDTO;
import edu.fscj.cen4940.capstone.dto.UserDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDTO me(@AuthenticationPrincipal UserDetails principal) {
        // Adjust if you store email vs username in your UserDetails
        String identifier = principal.getUsername();
        User user = userService.findByUsername(identifier);
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
                user.getMeasurementSystem()
        );
    }

    @PutMapping
    public UserDTO updateMe(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal,
            @RequestBody UpdateProfileDTO req // see DTO below
    ) {
        String id = principal.getUsername();
        User user = userService.findByUsername(id);

        // Only allow editable fields from the profile page
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setHeight(req.getHeight());
        user.setWeight(req.getWeight());
        user.setGoalWeight(req.getGoalWeight());
        user.setMeasurementSystem(req.getMeasurementSystem());

        User saved = userService.save(user);
        return new UserDTO(
                saved.getUsername(), saved.getFirstName(), saved.getLastName(),
                saved.getEmail(), saved.getDateOfBirth(), saved.getAge(),
                saved.getInitialWeight(),saved.getWeight(), saved.getGoalWeight(),
                saved.getHeight(), saved.getMeasurementSystem()
        );
    }
}