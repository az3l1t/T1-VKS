package net.az3l1t.slots_server.service.g;

import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
    void processEmployeeSchedule(MultipartFile file);
}
