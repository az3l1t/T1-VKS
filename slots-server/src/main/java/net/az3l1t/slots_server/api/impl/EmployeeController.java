package net.az3l1t.slots_server.api.impl;

import net.az3l1t.slots_server.service.impl.EmployeeService;
import net.az3l1t.slots_server.service.impl.SlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final SlotService slotService;

    public EmployeeController(EmployeeService employeeService, SlotService slotService) {
        this.employeeService = employeeService;
        this.slotService = slotService;
    }

    @PostMapping("/uploadSchedule")
    public ResponseEntity<String> uploadEmployeeSchedule(@RequestParam("file") MultipartFile file) {
        try {
            employeeService.processEmployeeSchedule(file);
            return ResponseEntity.ok("Schedule processed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}