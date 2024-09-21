package net.az3l1t.notification_server.service.impl;

import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class IcsGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IcsGenerator.class);


//    private static final DateTimeFormatter ICS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public File generateIcsFile(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String id) {
        logger.info("Generating ICS file for meeting between {} and {}", bookedBy, employee);

        // Преобразуем строки в LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        // Форматируем время для ICS (UTC)
        DateTimeFormatter icsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String formattedStartTime = start.format(icsFormatter);
        String formattedEndTime = end.format(icsFormatter);

        // Генерация уникального идентификатора события
//        String uid = UUID.randomUUID().toString();

        // Создание ICS контента
        StringBuilder icsContent = new StringBuilder();
        icsContent.append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//Meeting Scheduler//EN\n")
                .append("CALSCALE:GREGORIAN\n")
                .append("BEGIN:VEVENT\n")
                .append("UID:").append(id).append("\n")
                .append("DTSTAMP:").append(LocalDateTime.now().format(icsFormatter)).append("\n")
                .append("DTSTART:").append(formattedStartTime).append("\n")
                .append("DTEND:").append(formattedEndTime).append("\n")
                .append("SUMMARY:Meeting with ").append(employee).append("\n")
                .append("DESCRIPTION:Meeting between ").append(bookedBy).append(" and ").append(employee).append("\n")
                .append("ORGANIZER:MAILTO:").append(employeeEmail).append("\n")
                .append("ATTENDEE:MAILTO:").append(bookedByEmail).append("\n")
                .append("END:VEVENT\n")
                .append("END:VCALENDAR");

        // Сохранение контента в файл
        File icsFile = null;
        try {
            icsFile = File.createTempFile("meeting", ".ics");
            try (FileWriter writer = new FileWriter(icsFile)) {
                writer.write(icsContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("ICS file generated: {}", icsFile.getAbsolutePath());
        return icsFile;
    }

    public File generateIcsFile(String startTime, String employee, String employeeEmail) {
        logger.info("Generating ICS file for meeting between {} and {}", startTime, employee);
        // Преобразуем строку startTime в LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);

        // Рассчитываем endTime как startTime + 30 минут
        LocalDateTime end = start.plusMinutes(30);

        // Форматируем время для ICS (UTC)
        DateTimeFormatter icsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String formattedStartTime = start.format(icsFormatter);
        String formattedEndTime = end.format(icsFormatter);

        // Генерация уникального идентификатора события
        String uid = UUID.randomUUID().toString();

        // Создание ICS контента
        StringBuilder icsContent = new StringBuilder();
        icsContent.append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//Meeting Scheduler//EN\n")
                .append("CALSCALE:GREGORIAN\n")
                .append("BEGIN:VEVENT\n")
                .append("UID:").append(uid).append("\n")
                .append("DTSTAMP:").append(LocalDateTime.now().format(icsFormatter)).append("\n")
                .append("DTSTART:").append(formattedStartTime).append("\n")
                .append("DTEND:").append(formattedEndTime).append("\n")
                .append("SUMMARY:Work Slot for ").append(employee).append("\n")
                .append("DESCRIPTION:Work slot scheduled for ").append(employee).append("\n")
                .append("ORGANIZER:MAILTO:").append(employeeEmail).append("\n")
                .append("END:VEVENT\n")
                .append("END:VCALENDAR");

        // Сохранение контента в файл
        File icsFile = null;
        try {
            icsFile = File.createTempFile("work_slot", ".ics");
            try (FileWriter writer = new FileWriter(icsFile)) {
                writer.write(icsContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("ICS file generated: {}", icsFile.getAbsolutePath());
        return icsFile;
    }

    public File generateCancelIcsFile(String bookedBy, String bookedByEmail, String startTime, String endTime, String employee, String employeeEmail, String originalUID) throws IOException {
        logger.info("Generating ICS file for meeting cancellation between {} and {}", bookedBy, employee);

        // Преобразуем строки startTime и endTime в LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

        // Форматируем время для ICS (UTC)
        DateTimeFormatter icsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String startTimestamp = startDateTime.format(icsFormatter);
        String endTimestamp = endDateTime.format(icsFormatter);
        String dtStamp = LocalDateTime.now().format(icsFormatter);

        // Создаем содержимое ICS-файла с помощью StringBuilder
        StringBuilder icsContent = new StringBuilder();
        icsContent.append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//Your Company//NONSGML Event//EN\n")
                .append("METHOD:CANCEL\n")
                .append("BEGIN:VEVENT\n")
                .append("UID:").append(originalUID).append("\n")  // Используем тот же UID, что и в оригинальном событии
                .append("SEQUENCE:").append(1).append("\n")  // Указываем увеличенную последовательность
                .append("STATUS:CANCELLED\n")
                .append("SUMMARY:Cancelled: Meeting with ").append(bookedBy).append("\n")
                .append("DESCRIPTION:This meeting was scheduled with ").append(bookedBy).append(" but has been cancelled.\n")
                .append("DTSTAMP:").append(dtStamp).append("\n")  // Временная метка времени отмены
                .append("DTSTART:").append(startTimestamp).append("\n")
                .append("DTEND:").append(endTimestamp).append("\n")
                .append("LOCATION:Virtual\n")
                .append("ORGANIZER:MAILTO:").append(bookedByEmail).append("\n")  // Добавляем поле ORGANIZER
                .append("ATTENDEE;CN=").append(bookedBy).append(";RSVP=TRUE:mailto:").append(bookedByEmail).append("\n")
                .append("ATTENDEE;CN=").append(employee).append(";RSVP=TRUE:mailto:").append(employeeEmail).append("\n")
                .append("END:VEVENT\n")
                .append("END:VCALENDAR");

        // Сохраняем содержимое в файл
        File icsFile = File.createTempFile("canceled_meeting", ".ics");
        try (FileWriter writer = new FileWriter(icsFile)) {
            writer.write(icsContent.toString());
        }

        logger.info("ICS file generated: {}", icsFile.getAbsolutePath());
        return icsFile;
    }
}
