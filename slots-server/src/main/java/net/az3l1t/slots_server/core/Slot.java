package net.az3l1t.slots_server.core;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "slots")
@Getter
@Setter
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Start time cannot be null")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @FutureOrPresent(message = "End time must be in the present or future")
    private LocalDateTime endTime;

    private boolean booked;

    @Size(max = 100, message = "Booked by cannot exceed 100 characters")
    private String bookedBy;

    @Size(max = 100, message = "Employee name cannot exceed 100 characters")
    private String employeeName;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    private String employeeEmail;

    @Column(name = "calendar_event_id")  // Добавлено поле для хранения ID события
    private String calendarEventId;

    public Slot(Integer id, LocalDateTime startTime, LocalDateTime endTime, boolean booked, String bookedBy, String employeeName, String employeeEmail, String calendarEventId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.calendarEventId = calendarEventId;

    }

    public Slot(Integer id, LocalDateTime startTime, LocalDateTime endTime, boolean booked, String bookedBy, String employeeName, String employeeEmail) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
    }


    public Slot() {
    }

    public Slot(Integer id, LocalDateTime startTime, LocalDateTime endTime, boolean booked, String bookedBy, String employeeName) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.employeeName = employeeName;
    }

    public Slot(LocalDateTime startTime, LocalDateTime endTime, boolean booked, String bookedBy, String employeeName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.employeeName = employeeName;
    }

    public Slot(LocalDateTime startTime, LocalDateTime endTime, boolean booked, String bookedBy, String employeeName, String employeeEmail) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.bookedBy = bookedBy;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
    }
}