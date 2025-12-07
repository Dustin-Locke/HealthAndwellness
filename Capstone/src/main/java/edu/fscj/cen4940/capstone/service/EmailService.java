package edu.fscj.cen4940.capstone.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    // Logger for reminders
    private static final Logger reminderLogger = LoggerFactory.getLogger("REMINDER_EMAIL");

    // Logger for authentication/verification emails
    private static final Logger authLogger = LoggerFactory.getLogger("AUTH_EMAIL");

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Verify Your Email");
            helper.setText("<h2>Verify Your Email</h2>"
                    + "<p>Your verification code is: <b>" + code + "</b></p>", true);
            mailSender.send(message);
            authLogger.info("Verification email sent to {}", to);
        } catch (MessagingException e) {
            authLogger.error("Failed to send verification email to {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendReminderEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);

            // HTML template
            String htmlBody = "<html>"
                    + "<body style='font-family:Arial,sans-serif; line-height:1.5;'>"
                    + "<h2 style='color:#2E86C1;'>" + subject + "</h2>"
                    + "<p>" + body + "</p>"
                    + "<hr style='border:none; border-top:1px solid #ccc;'/>"
                    + "<p style='font-size:0.9em; color:#555;'>"
                    + "This is an automated reminder from The Reboot Clinic.</p>"
                    + "</body></html>";

            helper.setText(htmlBody, true);
            mailSender.send(message);
            reminderLogger.info("Reminder email sent to {} with subject '{}'", to, subject);

        } catch (MessagingException e) {
            reminderLogger.error("Failed to send email to {} with subject '{}': {}", to, subject, e.getMessage(), e);
        }
    }
}

