package net.az3l1t.authentication_server.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;

    private String email;
    private String phone;
    private String firstName;
    private String lastName;
}
