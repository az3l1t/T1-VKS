package net.az3l1t.authentication_server.api;

import jakarta.validation.Valid;
import net.az3l1t.authentication_server.repository.model.*;
import net.az3l1t.authentication_server.repository.model.PUT.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationController {
    public CompletableFuture<ResponseEntity<AuthenticationResponse>> register(AuthenticationRequest request);
    public CompletableFuture<ResponseEntity<LoginResponse>> login(LoginRequest request);
    public EmailResponse findEmail(String username);
    public Boolean hasSlotOrNuh(String username);
    public void changeTheBooleanToTrue(String username);
    public void changeTheBooleanToFalse(String username);

    CompletableFuture<ResponseEntity<BooleanResponse>> changeTheFirstname(FirstnameDTO firstnameDTO);
    CompletableFuture<ResponseEntity<BooleanResponse>>  changeTheLastname(LastnameDTO lastnameDTO);
    CompletableFuture<ResponseEntity<BooleanResponse>>  changeTheEmail(EmailDTO  emailDTO);
    CompletableFuture<ResponseEntity<BooleanResponse>>  changeThePhone(PhoneDTO phoneDTO);
    CompletableFuture<ResponseEntity<BooleanResponse>>  changeThePassword(PasswordDTO passwordDTO);}
