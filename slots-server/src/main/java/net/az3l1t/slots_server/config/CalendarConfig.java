package net.az3l1t.slots_server.config;

import com.google.api.services.calendar.Calendar;
import net.az3l1t.slots_server.service.impl.GoogleCalendarService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class CalendarConfig {

    @Bean
    public Calendar calendar() throws GeneralSecurityException, IOException {
        return GoogleCalendarService.getCalendarService();
    }
}