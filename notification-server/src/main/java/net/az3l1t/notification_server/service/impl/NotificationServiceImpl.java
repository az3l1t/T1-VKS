package net.az3l1t.notification_server.service.impl;

import jakarta.mail.internet.MimeMessage;
import net.az3l1t.notification_server.service.g.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    private final IcsGenerator icsGenerator;

    public NotificationServiceImpl(JavaMailSender mailSender, IcsGenerator icsGenerator) {
        this.mailSender = mailSender;
        this.icsGenerator = icsGenerator;
    }

    @Override
    public void processMessage(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String slotId) {
        logger.info("Processing message for meeting between {} and {}", bookedBy, employee);

        File icsFile = icsGenerator.generateIcsFile(bookedBy, bookedByEmail, startTime, endTime, employee, employeeEmail, slotId);

        sendEmailWithAttachment(employeeEmail, "Meeting Scheduled", "You have a meeting scheduled with " + bookedBy, icsFile);
        sendEmailWithAttachment(bookedByEmail, "Meeting Scheduled", "You have a meeting scheduled with " + employee, icsFile);

        logger.info("Message processed and emails sent for meeting between {} and {}", bookedBy, employee);
    }

    @Override
    public void processMessageCancelling(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String originalId) throws IOException {
        logger.info("Processing cancellation for meeting between {} and {}", bookedBy, employee);

        File icsFile = icsGenerator.generateCancelIcsFile(bookedBy, bookedByEmail, startTime, endTime, employee, employeeEmail, originalId);

        sendEmailWithAttachment(employeeEmail, "Meeting canceled","Meeting was canceled", icsFile);
        sendEmailWithAttachment(bookedByEmail, "Meeting canceled", "Meeting was canceled by " + employee, icsFile);

        logger.info("Cancellation processed and emails sent for meeting between {} and {}", bookedBy, employee);
    }

    @Override
    public void sendEmailWithAttachment(String toEmail, String subject, String body, File attachment) {
        logger.info("Sending email to {}", toEmail);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eternity_cr9p@mail.ru");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(attachment);
            helper.addAttachment(attachment.getName(), file);

            mailSender.send(message);
            logger.info("Email successfully sent to {}", toEmail);
        } catch (jakarta.mail.MessagingException e) {
            logger.error("Failed to send email to {}", toEmail, e);
        }
    }

    @KafkaListener(topics = "employee-topic", groupId = "notification_group")
    public void handleEmployeeTopicMessage(String message) {
        logger.info("Received message from employee-topic: {}", message);

        String[] parts = message.split(",");
        if (parts.length != 3) {
            logger.error("Invalid message format: {}", message);
            return;
        }

        String startTime = parts[0].trim();
        String employee = parts[1].trim();
        String employeeEmail = parts[2].trim();

        File icsFile = icsGenerator.generateIcsFile(startTime, employee, employeeEmail);

        String subject = "You have been scheduled for a work slot";
        String body = "Dear " + employee + ",\n\nYou have been scheduled for a work slot starting in" +
                startTime;

        sendEmailWithAttachment(employeeEmail, subject, body, icsFile);
        logger.info("Handled message for employee {} with email {}", employee, employeeEmail);
    }
}
