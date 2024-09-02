package net.az3l1t.slots_server.service.impl;

import net.az3l1t.slots_server.repository.SlotRepository;
import org.joda.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SlotCleaningSystem {
    private final SlotRepository slotRepository;

    public SlotCleaningSystem(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    // Метод для удаления устаревших слотов
    @Scheduled(cron = "0 0 0 * * ?") // Выполняется каждый день в полночь
    public void deleteExpiredSlots() {
        LocalDateTime now = LocalDateTime.now();
        slotRepository.deleteByEndTimeBefore(now);
    }
}
