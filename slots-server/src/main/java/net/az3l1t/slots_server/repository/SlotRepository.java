package net.az3l1t.slots_server.repository;

import net.az3l1t.slots_server.core.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Integer> {
    // Поиск слота по начальному и конечному времени
    Slot findByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);

    // Поиск слотов по интервалу времени
    List<Slot> findAllByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    void deleteByEndTimeBefore(org.joda.time.LocalDateTime now);

    Slot findByBookedBy(String bookedBy);
}
