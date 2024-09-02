package net.az3l1t.slots_server.client;

import net.az3l1t.slots_server.repository.model.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "auth-server", url = "http://auth-server:8000")
public interface AuthenticationClient {
    @GetMapping("/api/v1/auth/getEmail")
    EmailResponse findEmail(@RequestParam("username") String username);

    @GetMapping("/api/v1/auth/getBoolean")
    Boolean hasSlotOrNuh(@RequestParam("username") String username);

    @PostMapping("/api/v1/auth/changeTheBooleanToTrue")
    void changeTheBooleanToTrue(@RequestParam("username") String username);

    @PostMapping("/api/v1/auth/changeTheBooleanToFalse")
    void changeTheBooleanToFalse(@RequestParam("username") String username);
}