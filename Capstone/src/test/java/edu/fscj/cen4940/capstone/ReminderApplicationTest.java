package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.Reminder;
import edu.fscj.cen4940.capstone.enums.MeasurementSystem;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import edu.fscj.cen4940.capstone.repository.ReminderRepository;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.util.Create;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("Reminder Repository CRUD Tests")
public class ReminderApplicationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private CreateAndPersist createAndPersist;

    @Test
    @DisplayName("Save reminder with user")
    void saveReminder_ShouldSaveReminder() {
        Reminder savedReminder = createAndPersist.workoutReminder(
                "save",
                LocalTime.of(8, 0),
                true);
        Reminder foundReminder = entityManager.find(Reminder.class, savedReminder.getId());

        assertThat(foundReminder).isEqualTo(savedReminder);
        assertThat(foundReminder.getUser()).isEqualTo(savedReminder.getUser());
    }

    @Test
    @DisplayName("findById returns empty if missing")
    void findById_ShouldReturnEmpty_WhenReminderDoesNotExist() {
        Optional<Reminder> reminder = reminderRepository.findById(9999);
        assertThat(reminder).isEmpty();
    }

    @Test
    @DisplayName("findByUserId returns all reminders")
    void findByUserId_ShouldReturnRemindersForUser() {
        Reminder savedReminder1 = createAndPersist.workoutReminder(
                "find",
                LocalTime.of(8, 0),
                true);

        Reminder savedReminder2 = createAndPersist.mealLogReminder(
                "find",
                LocalTime.of(7, 0),
                false);

        Integer userId = savedReminder1.getUser().getId();

        List<Reminder> reminders = reminderRepository.findByUserId(userId);
        assertThat(reminders)
                .hasSize(2)
                .containsExactlyInAnyOrder(savedReminder1, savedReminder2);
    }

    @Test
    @DisplayName("findByUserId returns empty when user has no reminders")
    void findByUserId_ShouldReturnEmpty_WhenUserHasNoReminders() {
        User user = createAndPersist.user("emptyuser");
        List<Reminder> reminders = reminderRepository.findByUserId(user.getId());
        assertThat(reminders).isEmpty();
    }

    @Test
    @DisplayName("Enabled reminders after time")
    void findByUserIdAndEnabledTrueAndNotifyTimeAfter_ShouldReturnMatchingReminders() {
        Reminder savedReminder1 = createAndPersist.workoutReminder(
                "findafter",
                LocalTime.of(10, 0),
                true);

        Reminder savedReminder2 = createAndPersist.mealLogReminder(
                "findafter",
                LocalTime.of(8, 0),
                true);

        Integer userId = savedReminder1.getUser().getId();

        List<Reminder> remindersAfter9 = reminderRepository
                .findByUserIdAndEnabledTrueAndNotifyTimeAfter(
                        userId,
                        LocalTime.of(9, 0));
        assertThat(remindersAfter9).containsExactly(savedReminder1);
    }

    @Test
    @DisplayName("Enabled reminders after time returns empty when none match")
    void findByUserIdAndEnabledTrueAndNotifyTimeAfter_ShouldReturnEmpty_WhenNoRemindersMatchTime() {
        Reminder savedReminder = createAndPersist.workoutReminder(
                "notime",
                LocalTime.of(6, 0),
                true);

        Integer userId = savedReminder.getUser().getId();

        List<Reminder> remindersAfter9 = reminderRepository
                .findByUserIdAndEnabledTrueAndNotifyTimeAfter(
                        userId,
                        LocalTime.of(9, 0));
        assertThat(remindersAfter9).isEmpty();
    }

    @Test
    @DisplayName("Delete all reminders for user")
    void deleteByUserId_ShouldRemoveAllUserReminders() {
        Reminder savedReminder1 = createAndPersist.workoutReminder(
                "delete",
                LocalTime.of(8, 0),
                true);

        Reminder savedReminder2 = createAndPersist.mealLogReminder(
                "delete",
                LocalTime.of(7, 0),
                true);

        Integer userId = savedReminder1.getUser().getId();

        reminderRepository.deleteByUserId(userId);

        List<Reminder> reminders = reminderRepository.findByUserId(userId);
        assertThat(reminders).isEmpty();
    }

    @Test
    @DisplayName("deleteByUserId does not fail when user has no reminders")
    void deleteByUserId_ShouldNotFail_WhenUserHasNoReminders() {
        User user = createAndPersist.user("nodelete");

        // Attempt to delete, even though user has no reminders
        reminderRepository.deleteByUserId(user.getId());

        List<Reminder> reminders = reminderRepository.findByUserId(user.getId());
        assertThat(reminders).isEmpty();
    }
}
