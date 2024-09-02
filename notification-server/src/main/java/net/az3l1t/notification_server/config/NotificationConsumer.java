package net.az3l1t.notification_server.config;

import net.az3l1t.notification_server.service.impl.NotificationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final ConcurrentLinkedQueue<String> queueForMessages = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queueForCancelling = new ConcurrentLinkedQueue<>();

    private final NotificationServiceImpl notificationService;

    public NotificationConsumer(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consumeNotification(String message) {
        logger.info("Consumed notification message: {}", message);
        // add info in queue
        queueForMessages.add(message);

        // check if there are 6 messages
        if (queueForMessages.size() >= 7) {
            String bookedBy = queueForMessages.poll();
            String bookedByEmail = queueForMessages.poll();
            String startTime = queueForMessages.poll();
            String endTime = queueForMessages.poll();
            String employee = queueForMessages.poll();
            String employeeEmail = queueForMessages.poll();
            String slotId = queueForMessages.poll();
            notificationService.processMessage(bookedBy, bookedByEmail, startTime, endTime, employee, employeeEmail, slotId);
        }
    }

    @KafkaListener(topics = "cancelling-topic", groupId = "notification-group")
    public void consumeCancelling(String message) throws IOException {
        logger.info("Consumed notification message: {}", message);
        // add info in queue
        queueForCancelling.add(message);

        // check if there are 6 messages
        if (queueForCancelling.size() >= 7) {
            String bookedBy = queueForCancelling.poll();
            String bookedByEmail = queueForCancelling.poll();
            String startTime = queueForCancelling.poll();
            String endTime = queueForCancelling.poll();
            String employee = queueForCancelling.poll();
            String employeeEmail = queueForCancelling.poll();
            String slotId = queueForCancelling.poll();

            notificationService.processMessageCancelling(bookedBy, bookedByEmail, startTime, endTime, employee, employeeEmail, slotId);
        }
    }

}