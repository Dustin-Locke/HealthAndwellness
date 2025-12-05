package edu.fscj.cen4940.capstone.service;

import edu.fscj.cen4940.capstone.dto.ReminderDTO;
import edu.fscj.cen4940.capstone.entity.Reminder;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import edu.fscj.cen4940.capstone.repository.ReminderRepository;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    private ReminderDTO convertToDTO(Reminder reminder) {
        ReminderDTO dto = new ReminderDTO(
                reminder.getId(),
                reminder.getUser().getId(),
                reminder.getType(),
                reminder.getEnabled(),
                reminder.getFrequency(),
                reminder.getNotifyTime(),
                reminder.getNotifyDate(),
                reminder.getLastNotified(),
                reminder.getTitle()
        );

        dto.setMessage(reminder.getType().getDefaultMessage());

        return dto;
    }

    public ReminderDTO save(ReminderDTO dto) {
        Reminder reminder = new Reminder();

        // Resolve user from userId
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        reminder.setUser(user);

        // Set core fields
        reminder.setType(dto.getType());
        reminder.setEnabled(dto.getEnabled());
        reminder.setFrequency(dto.getFrequency());
        reminder.setNotifyTime(dto.getNotifyTime());
        reminder.setNotifyDate(dto.getNotifyDate());
        reminder.setLastNotified(dto.getLastNotified());
        reminder.setTitle(dto.getTitle());

        // Set message from ReminderType default if not explicitly provided
        if (dto.getMessage() != null && !dto.getMessage().isBlank()) {
            reminder.setMessage(dto.getMessage());
        } else if (dto.getType() != null) {
            reminder.setMessage(dto.getType().getDefaultMessage());
        }

        Reminder saved = reminderRepository.save(reminder);
        return convertToDTO(saved);
    }

    public ReminderDTO update(Integer id, ReminderDTO dto) {

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        reminder.setUser(user);
        reminder.setType(dto.getType());
        reminder.setEnabled(dto.getEnabled());
        reminder.setFrequency(dto.getFrequency());
        reminder.setNotifyTime(dto.getNotifyTime());
        reminder.setNotifyDate(dto.getNotifyDate());
        reminder.setLastNotified(dto.getLastNotified());
        reminder.setTitle(dto.getTitle());
        reminder.setMessage(dto.getMessage());

        Reminder saved = reminderRepository.save(reminder);
        return convertToDTO(saved);
    }


    public Optional<ReminderDTO> getById(Integer id) {
        return reminderRepository.findById(id)
                .map(this::convertToDTO);
    }


    public List<ReminderDTO> getByUser(User user) {
        return reminderRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ReminderDTO> getEnabledByUser(User user) {
        return reminderRepository.findByUserIdAndEnabledTrue(user.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ReminderDTO> getByType(User user, ReminderType type) {
        return reminderRepository.findByUserIdAndType(user.getId(), type)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ReminderDTO> getUpcomingReminders(User user, LocalTime currentTime) {
        return reminderRepository.findByUserIdAndEnabledTrueAndNotifyTimeAfter(user.getId(), currentTime)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<ReminderDTO> markNotified(Integer id, LocalDate date) {
        return reminderRepository.findById(id)
                .map(reminder -> {
                    reminder.setLastNotified(date);
                    return convertToDTO(reminderRepository.save(reminder));
                });
    }


    public void deleteById(Integer id) {
        if (!reminderRepository.existsById(id)) {
            throw new RuntimeException("Reminder not found");
        }
        reminderRepository.deleteById(id);
    }

}

