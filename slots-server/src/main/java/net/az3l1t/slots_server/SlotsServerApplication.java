package net.az3l1t.slots_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class SlotsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlotsServerApplication.class, args);
	}

}
