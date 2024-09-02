package net.az3l1t.slots_server.service.impl;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import net.az3l1t.slots_server.client.AuthenticationClient;
import net.az3l1t.slots_server.core.Slot;
import net.az3l1t.slots_server.repository.SlotRepository;
import net.az3l1t.slots_server.repository.model.EmailResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.ArrayList;

@Service
public class SlotService implements net.az3l1t.slots_server.service.g.SlotService {

    private final SlotRepository slotRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AuthenticationClient authenticationClient;
    private final Calendar calendar;

    public SlotService(SlotRepository slotRepository, KafkaTemplate<String, String> kafkaTemplate, AuthenticationClient authenticationClient, Calendar calendar) {
        this.slotRepository = slotRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.authenticationClient = authenticationClient;
        this.calendar = calendar;
    }

    // finding slots for day
    public List<Slot> getSlotsForDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Slot> existingSlots = slotRepository.findAllByStartTimeBetween(startOfDay, endOfDay);

        // if there is no slots - generating
        if (existingSlots.isEmpty()) {
            List<Slot> newSlots = generateStandardSlots(date);
            slotRepository.saveAll(newSlots);
            return newSlots;
        }
        // return all slots
        return existingSlots;
    }

    public String handleSlotBooking(LocalDate date, LocalTime time, String bookedBy) throws IOException {
        if (!isValidTime(time)) {
            return "Error: Invalid booking time. Available times are 12:00, 13:00, 15:00, and 16:00.";
        }

        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusMinutes(30);

        Slot slot = slotRepository.findByStartTimeAndEndTime(startTime, endTime);

        if (slot != null) {
            if (slot.isBooked()) {
                return "Slot is already booked";
            } else {
                slot.setBooked(true);
                slot.setBookedBy(bookedBy);

                // Create event in calendar
                Event event = new Event()
                        .setSummary("Slot Booking")
                        .setLocation("Your Location")
                        .setDescription("Slot booked by " + bookedBy);

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

                // Save event ID in the database
                slot.setCalendarEventId(createdEvent.getId());

                slotRepository.save(slot);
                sendToKafka(slot, String.valueOf(slot.getCalendarEventId()));
                authenticationClient.changeTheBooleanToTrue(bookedBy);
                return "Slot booked successfully";
            }
        } else {
            Slot newSlot = new Slot(startTime, endTime, true, bookedBy, null);
            slotRepository.save(newSlot);
            String slotId = String.valueOf(newSlot.getId());

            List<Slot> existingSlots = slotRepository.findAllByStartTimeBetween(date.atStartOfDay(), date.atTime(LocalTime.MAX));
            if (existingSlots.size() < 4) {
                List<Slot> newSlots = generateStandardSlots(date);
                for (Slot s : newSlots) {
                    if (existingSlots.stream().noneMatch(existing -> existing.getStartTime().equals(s.getStartTime()))) {
                        slotRepository.save(s);
                    }
                }
            }

            sendToKafka(newSlot, slotId);
            authenticationClient.changeTheBooleanToTrue(bookedBy);
            return "Slot created and booked successfully";
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


    public String cancelSlotBooking(LocalDate date, LocalTime time, String bookedBy) throws IOException {
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusMinutes(30);

        Slot slot = slotRepository.findByStartTimeAndEndTime(startTime, endTime);

        EmailResponse emailResponse = authenticationClient.findEmail(slot.getBookedBy());
        String userEmail = emailResponse.getEmail();

        if (slot != null) {
            if (slot.isBooked() && slot.getBookedBy().equals(bookedBy)) {

                String slotId = String.valueOf(slot.getCalendarEventId());
                sendToKafkaCancelling(slot, userEmail, slotId);

                calendar.events().delete("primary", slotId).execute();

                slot.setBooked(false);
                slot.setBookedBy(null);
                slotRepository.save(slot);
                authenticationClient.changeTheBooleanToFalse(bookedBy);

                return "Slot booking canceled successfully";
            } else {
                return "Error: Slot is not booked or booked by another user";
            }
        } else {
            return "Error: Slot not found";
        }
    }

    public void sendToKafkaCancelling(Slot slot, String userEmail, String slotId) {
        try {
            kafkaTemplate.send("cancelling-topic", "bookedBy", slot.getBookedBy()).get();
            System.out.println("Sent bookedBy");

            kafkaTemplate.send("cancelling-topic", "bookedByEmail", userEmail).get();
            System.out.println("Sent bookedByEmail");

            kafkaTemplate.send("cancelling-topic", "startTime", String.valueOf(slot.getStartTime())).get();
            System.out.println("Sent startTime");

            kafkaTemplate.send("cancelling-topic", "endTime", String.valueOf(slot.getEndTime())).get();
            System.out.println("Sent endTime");

            kafkaTemplate.send("cancelling-topic", "employee", slot.getEmployeeName()).get();
            System.out.println("Sent employee");

            kafkaTemplate.send("cancelling-topic", "employeeEmail", slot.getEmployeeEmail()).get();
            System.out.println("Sent employeeEmail");

            kafkaTemplate.send("cancelling-topic", "slotId", slotId).get();
            System.out.println("Sent slotId");
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public void sendToKafka(Slot slot, String id) {
        try {
            EmailResponse emailResponse = authenticationClient.findEmail(slot.getBookedBy());
            String userEmail = emailResponse.getEmail();

            kafkaTemplate.send("notification-topic", "bookedBy", slot.getBookedBy()).get();
            System.out.println("Sent bookedBy");

            kafkaTemplate.send("notification-topic", "bookedByEmail", userEmail).get();
            System.out.println("Sent bookedByEmail");

            kafkaTemplate.send("notification-topic", "startTime", String.valueOf(slot.getStartTime())).get();
            System.out.println("Sent startTime");

            kafkaTemplate.send("notification-topic", "endTime", String.valueOf(slot.getEndTime())).get();
            System.out.println("Sent endTime");

            kafkaTemplate.send("notification-topic", "employee", slot.getEmployeeName()).get();
            System.out.println("Sent employee");

            kafkaTemplate.send("notification-topic", "employeeEmail", slot.getEmployeeEmail()).get();
            System.out.println("Sent employeeEmail");

            kafkaTemplate.send("notification-topic", "slotId", id).get();
            System.out.println("Sent slotId");

        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    // checking slots
    public boolean isValidTime(LocalTime time) {
        LocalTime[] validTimes = {
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
        };

        for (LocalTime validTime : validTimes) {
            if (validTime.equals(time)) {
                return true;
            }
        }

        return false;
    }

    // generating slots
    public List<Slot> generateStandardSlots(LocalDate date) {
        List<Slot> slots = new ArrayList<>();

        // intervals for slots
        LocalTime[] intervals = {
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
        };

        for (LocalTime time : intervals) {
            LocalDateTime slotStartTime = LocalDateTime.of(date, time);
            LocalDateTime slotEndTime = slotStartTime.plusMinutes(30);

            Slot slot = new Slot(slotStartTime, slotEndTime, false, null, null);
            slots.add(slot);
        }

        return slots;
    }

    public Slot findSlotByUsername(String bookedBy) {
        return slotRepository.findByBookedBy(bookedBy);
    }
}
