package net.az3l1t.authentication_server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserWasNotFoundException extends RuntimeException {
    public UserWasNotFoundException(String message) {
        super(message);
    }
}


