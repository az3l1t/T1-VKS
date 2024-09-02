package net.az3l1t.slots_server.repository.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameRequest {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 50, message = "Username should be between 4 and 50 characters")
    private String username;
}
