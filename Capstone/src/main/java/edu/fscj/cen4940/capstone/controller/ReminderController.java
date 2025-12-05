package edu.fscj.cen4940.capstone.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.fscj.cen4940.capstone.dto.ReminderDTO;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import edu.fscj.cen4940.capstone.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "http://localhost:4200")  // <-- IMPORTANT for Angular
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    // Get all reminders for a user
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReminderDTO>> getAllByUser(@PathVariable Integer userId) {
        User user = new User();
        user.setId(userId);
        List<ReminderDTO> reminders = reminderService.getByUser(user);
        return ResponseEntity.ok(reminders); // 200 even if empty
    }

    // Get all enabled reminders for a user
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/enabled")
    public ResponseEntity<List<ReminderDTO>> getEnabledByUser(@PathVariable Integer userId) {
        User user = new User();
        user.setId(userId);
        List<ReminderDTO> reminders = reminderService.getEnabledByUser(user);
        return ResponseEntity.ok(reminders); // always 200, even if empty
    }


    // Get reminders by type
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<ReminderDTO>> getByType(@PathVariable Integer userId, @PathVariable ReminderType type) {
        User user = new User();
        user.setId(userId);
        List<ReminderDTO> reminders = reminderService.getByType(user, type);
        return ResponseEntity.ok(reminders);
    }

    // Get upcoming reminders
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReminderDTO>> getUpcoming(@PathVariable Integer userId,
                                                         @RequestParam("afterTime") String afterTime) {
        User user = new User();
        user.setId(userId);
        LocalTime currentTime = LocalTime.parse(afterTime);
        List<ReminderDTO> reminders = reminderService.getUpcomingReminders(user, currentTime);
        return ResponseEntity.ok(reminders);
    }

    // Get reminder by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReminderDTO> getById(@PathVariable Integer id) {
        return reminderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new reminder
    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody @Valid ReminderDTO dto) {
        ReminderDTO saved = reminderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update an existing reminder
    @PutMapping("/{id}")
    public ResponseEntity<ReminderDTO> updateReminder(@PathVariable Integer id,
                                                      @RequestBody @Valid ReminderDTO dto) {
        ReminderDTO updated = reminderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete a reminder
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Integer id) {
        try {
            reminderService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mark reminder as notified
    @PostMapping("/{id}/notified")
    public ResponseEntity<ReminderDTO> markNotified(
            @PathVariable Integer id,
            @RequestParam("date") String date) {

        return reminderService.markNotified(id, LocalDate.parse(date))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
