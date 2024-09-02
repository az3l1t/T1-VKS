package net.az3l1t.authentication_server.api.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.az3l1t.authentication_server.api.AuthenticationController;
import net.az3l1t.authentication_server.repository.UserRepository;
import net.az3l1t.authentication_server.repository.model.*;
import net.az3l1t.authentication_server.repository.model.PUT.*;
import net.az3l1t.authentication_server.service.impl.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController()
@RequestMapping("/api/v1/auth")
@Tag(name = "Контроллер аутентификации", description = "Регистрация и логин для пользователя")
public class AuthenticationControllerImpl implements AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationControllerImpl(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Позволяет зарегистрировать пользователя"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован!"),
                    @ApiResponse(responseCode = "400", description = "Такой пользователь уже существует или неверные данные!")
            }
    )
    public CompletableFuture<ResponseEntity<AuthenticationResponse>> register(@Valid @RequestBody AuthenticationRequest request) {
        return authenticationService.register(request);
    }

    @Override
    @PostMapping("/login")
    @Operation(
            summary = "Логин пользователя",
            description = "Позволяет залогинить пользователя"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешный логин!"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден!"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные!")
            }
    )
    public CompletableFuture<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.authenticate(request);
    }

    /*
    With no jwt
     */
    @Override
    @GetMapping("/getEmail")
    public EmailResponse findEmail(@Valid @RequestParam("username") String username) {
        return authenticationService.findEmailByUsername(username);
    }

    @Override
    @GetMapping("/getBoolean")
    public Boolean hasSlotOrNuh(@Valid @RequestParam("username") String username) {
        return authenticationService.findIfHasSlot(username);
    }

    @Override
    @PostMapping("/changeTheBooleanToTrue")
    public void changeTheBooleanToTrue(@Valid @RequestParam("username") String username) {
        authenticationService.changeTheSlotParamToTrue(username);
    }

    @Override
    @PostMapping("/changeTheBooleanToFalse")
    public void changeTheBooleanToFalse(@Valid @RequestParam("username") String username) {
        authenticationService.changeTheSlotParamToFalse(username);
    }

    /*
    With jwt
     */

    @Override
    @PostMapping("/changeTheFirstname")
    public CompletableFuture<ResponseEntity<BooleanResponse>>  changeTheFirstname(@Valid @RequestBody FirstnameDTO firstnameDTO) {
        return authenticationService.changeFirstname(firstnameDTO);
    }

    @Override
    @PostMapping("/changeTheLastname")
    public CompletableFuture<ResponseEntity<BooleanResponse>>  changeTheLastname(@Valid @RequestBody LastnameDTO lastnameDTO) {
        return authenticationService.changeLastname(lastnameDTO);
    }

    @Override
    @PostMapping("/changeTheEmail")
    public CompletableFuture<ResponseEntity<BooleanResponse>>  changeTheEmail(@Valid @RequestBody EmailDTO emailDTO) {
        return authenticationService.changeEmail(emailDTO);
    }

    @Override
    @PostMapping("/changeThePhone")
    public CompletableFuture<ResponseEntity<BooleanResponse>>  changeThePhone(@Valid @RequestBody PhoneDTO phoneDTO) {
        return authenticationService.changePhone(phoneDTO);
    }

    @Override
    @PostMapping("/changeThePassword")
    public CompletableFuture<ResponseEntity<BooleanResponse>>  changeThePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        return authenticationService.changePassword(passwordDTO);
    }

}
