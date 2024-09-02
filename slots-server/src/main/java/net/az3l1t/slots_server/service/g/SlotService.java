package net.az3l1t.slots_server.service.g;

import com.google.api.client.util.DateTime;
import net.az3l1t.slots_server.core.Slot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface SlotService {
    List<Slot> getSlotsForDay(LocalDate date);
    String handleSlotBooking(LocalDate date, LocalTime time, String bookedBy) throws IOException;
    boolean isValidTime(LocalTime time);
    List<Slot> generateStandardSlots(LocalDate date);
    String cancelSlotBooking(LocalDate date, LocalTime time, String bookedBy) throws IOException;
    DateTime convertToEventDateTime(LocalDateTime localDateTime);

}
