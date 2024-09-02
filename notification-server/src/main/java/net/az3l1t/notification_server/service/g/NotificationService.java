package net.az3l1t.notification_server.service.g;

import java.io.File;
import java.io.IOException;

public interface NotificationService {
    void processMessage(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String slotId);
    void processMessageCancelling(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String originalId) throws IOException;
    void sendEmailWithAttachment(String toEmail, String subject, String body, File attachment);
}
