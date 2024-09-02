package net.az3l1t.slots_server.api.impl;

import net.az3l1t.slots_server.core.Slot;
import net.az3l1t.slots_server.service.impl.SlotService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @QueryMapping
    public List<Slot> getAvailableSlotsForDate(@Argument String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        return slotService.getSlotsForDay(selectedDate);
    }

    @QueryMapping
    public Slot getSlotPerUser(@Argument String bookedBy){
        return slotService.findSlotByUsername(bookedBy);
    }

    @MutationMapping
    public String handleSlotBooking(@Argument String date,
                                    @Argument String time,
                                    @Argument String bookedBy) throws IOException {
        LocalDate selectedDate = LocalDate.parse(date);
        LocalTime selectedTime = LocalTime.parse(time);

        return slotService.handleSlotBooking(selectedDate, selectedTime, bookedBy);
    }

    @MutationMapping
    public String cancelSlotBooking(@Argument String date,
                                    @Argument String time,
                                    @Argument String bookedBy) throws IOException {
        LocalDate selectedDate = LocalDate.parse(date);
        LocalTime selectedTime = LocalTime.parse(time);

        return slotService.cancelSlotBooking(selectedDate, selectedTime, bookedBy);

        // TODO - уведмоление об отмене + google calendar api


    }
}