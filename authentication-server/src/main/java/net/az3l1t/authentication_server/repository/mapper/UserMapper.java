package net.az3l1t.authentication_server.repository.mapper;

import net.az3l1t.authentication_server.core.User;
import net.az3l1t.authentication_server.repository.model.AuthenticationRequest;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toUserFromAuthRequest(AuthenticationRequest request) {
        return new User(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                null
        );
    }

}
