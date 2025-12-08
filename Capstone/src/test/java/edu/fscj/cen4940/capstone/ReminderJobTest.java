package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.entity.Reminder;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.ReminderRepository;
import edu.fscj.cen4940.capstone.scheduler.ReminderJob;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.ReminderService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Reminder Job Tests")
public class ReminderJobTest {

    @Autowired
    private ReminderJob reminderJob;

    @MockBean
    private ReminderRepository reminderRepository;

    @MockBean
    private ReminderService reminderService;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("ReminderJob should send email for eligible reminders")
    void testReminderJobSendsEmail() throws Exception {
        // Setup a mock reminder
        User user = new User();
        user.setEmail("testuser@example.com");

        Reminder reminder = new Reminder();
        reminder.setId(999999);
        reminder.setTitle("Test Reminder");
        reminder.setMessage("This is a test");
        reminder.setUser(user);
        reminder.setEnabled(true);

        List<Reminder> reminders = List.of(reminder);

        // Mock repository to return the reminder
        Mockito.when(reminderRepository.findByEnabledTrue()).thenReturn(reminders);
        // Mock the service to say it should send today
        Mockito.when(reminderService.shouldSendToday(Mockito.any(Reminder.class), Mockito.any(), Mockito.any()))
                .thenReturn(true);
        // Mock email service
        Mockito.doNothing().when(emailService).sendReminderEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        // Mock save to just return the same reminder
        Mockito.when(reminderRepository.save(Mockito.any(Reminder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the job
        reminderJob.execute(null);

        // Verify email sent
        Mockito.verify(emailService).sendReminderEmail("testuser@example.com", "Test Reminder", "This is a test");
        // Verify repository save called
        Mockito.verify(reminderRepository).save(Mockito.any(Reminder.class));
    }

    @Test
    @DisplayName("ReminderJob should skip reminders not due today")
    void testReminderJobSkipsNonEligibleReminders() throws Exception {
        Reminder reminder = new Reminder();
        reminder.setEnabled(true);

        Mockito.when(reminderRepository.findByEnabledTrue()).thenReturn(List.of(reminder));
        Mockito.when(reminderService.shouldSendToday(Mockito.any(Reminder.class), Mockito.any(), Mockito.any()))
                .thenReturn(false);

        reminderJob.execute(null);

        // Email should not be sent
        Mockito.verify(emailService, Mockito.never()).sendReminderEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        // Save should not be called
        Mockito.verify(reminderRepository, Mockito.never()).save(Mockito.any());
    }
}
