package edu.fscj.cen4940.capstone.scheduler;

import edu.fscj.cen4940.capstone.entity.Reminder;
import edu.fscj.cen4940.capstone.repository.ReminderRepository;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.ReminderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class ReminderJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ReminderJob.class);

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EmailService emailService; // You need a service to actually send emails

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDate today = LocalDate.now();
        LocalTime now = java.time.LocalTime.now();

        List<Reminder> reminders = reminderRepository.findByEnabledTrue();

        for (Reminder r : reminders) {
            try {
                if (reminderService.shouldSendToday(r, today, now)) {
                    emailService.sendReminderEmail(r.getUser().getEmail(), r.getTitle(), r.getMessage());

                    // Mark as notified
                    r.setLastNotified(today);
                    r.setLastNotifiedPeriod(r.getFrequency() != null ? r.getFrequency().name() : "ONCE");
                    reminderRepository.save(r);
                    logger.info("Sent reminder {} to {}", r.getId(), r.getUser().getEmail());
                }
            } catch (Exception e) {
                logger.error("Failed to send reminder {} to {}: {}", r.getId(), r.getUser().getEmail(), e.getMessage(), e);

            }
        }
    }


}
