package net.az3l1t.slots_server.service.impl;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import net.az3l1t.slots_server.core.Slot;
import net.az3l1t.slots_server.repository.SlotRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class EmployeeService implements net.az3l1t.slots_server.service.g.EmployeeService {

    private final SlotRepository slotRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Calendar calendar;

    public EmployeeService(SlotRepository slotRepository, KafkaTemplate<String, String> kafkaTemplate, Calendar calendar) {
        this.slotRepository = slotRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.calendar = calendar;
    }

    @Override
    public void processEmployeeSchedule(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Uploaded file is not a CSV");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);

                if (parts.length != 3) {
                    throw new IllegalArgumentException("Each line should contain exactly 3 values");
                }

                String employeeName = parts[0].trim();
                LocalDateTime startTime = LocalDateTime.parse(parts[1].trim());
                String employeeEmail = parts[2].trim();

                LocalDateTime endTime = startTime.plusMinutes(30);

                Slot existingSlot = slotRepository.findByStartTimeAndEndTime(startTime, endTime);

                if (existingSlot != null) {
                    // Create event in calendar
                    Event event = new Event()
                            .setSummary("Slot Booking")
                            .setLocation("Your Location")
                            .setDescription("Slot booked by " + existingSlot.getBookedBy());

                    // Convert LocalDateTime to DateTime for Google Calendar API
                    EventDateTime start = new EventDateTime()
                            .setDateTime(convertToEventDateTime(startTime))
                            .setTimeZone(ZoneId.systemDefault().getId());

                    event.setStart(start);

                    EventDateTime end = new EventDateTime()
                            .setDateTime(convertToEventDateTime(endTime))
                            .setTimeZone(ZoneId.systemDefault().getId());

                    event.setEnd(end);

                    Event createdEvent = calendar.events().insert("primary", event).execute();

                    existingSlot.setEmployeeName(employeeName);
                    existingSlot.setEmployeeEmail(employeeEmail);
                    slotRepository.save(existingSlot);
                } else {
                    Slot slot = new Slot(startTime, endTime, false, null, employeeName, employeeEmail);
                    
                    Event event = new Event()
                            .setSummary("Slot Booking")
                            .setLocation("Your Location")
                            .setDescription("Slot booked by " + slot.getBookedBy());

                    // Convert LocalDateTime to DateTime for Google Calendar API
                    EventDateTime start = new EventDateTime()
                            .setDateTime(convertToEventDateTime(startTime))
                            .setTimeZone(ZoneId.systemDefault().getId());

                    event.setStart(start);

                    EventDateTime end = new EventDateTime()
                            .setDateTime(convertToEventDateTime(endTime))
                            .setTimeZone(ZoneId.systemDefault().getId());

                    event.setEnd(end);

                    Event createdEvent = calendar.events().insert("primary", event).execute();


                    slotRepository.save(slot);
                }

                // Отправка данных в Kafka
                sendToKafka(startTime.toString(), employeeName, employeeEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    public DateTime convertToEventDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

        // Convert to milliseconds since epoch
        long milliseconds = offsetDateTime.toInstant().toEpochMilli();

        // Create Google Calendar API DateTime object
        return new com.google.api.client.util.DateTime(milliseconds);
    }

    public void sendToKafka(String startTime, String employeeName, String employeeEmail) {
        try {
            String message = String.format("%s,%s,%s", startTime, employeeName, employeeEmail);
            kafkaTemplate.send("employee-topic", message);
            System.out.println("Sent message to Kafka: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message to Kafka: " + e.getMessage());
        }
    }

}