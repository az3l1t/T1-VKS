package net.az3l1t.authentication_server.service.impl;

import jakarta.annotation.PostConstruct;
import net.az3l1t.authentication_server.core.Role;
import net.az3l1t.authentication_server.core.User;
import net.az3l1t.authentication_server.exceptions.InvalidCredentialsException;
import net.az3l1t.authentication_server.exceptions.UserWasNotFoundException;
import net.az3l1t.authentication_server.repository.UserRepository;
import net.az3l1t.authentication_server.repository.mapper.UserMapper;
import net.az3l1t.authentication_server.repository.model.*;
import net.az3l1t.authentication_server.repository.model.PUT.*;
import net.az3l1t.authentication_server.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @PostConstruct
    public void init() {
        String adminUsername = "admin_l9dkcd";
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            String adminPassword = "admin_ls45jd2";
            admin.setPassword(passwordEncoder.encode(adminPassword));
            String adminEmail = "eternity_cr9p222@mail.ru";
            admin.setEmail(adminEmail);

            admin.setFirstName("AdminFirstName");
            admin.setLastName("AdminLastName");
            admin.setPhone("+79001234567");

            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminUsername);
        } else {
            System.out.println("Admin user already exists: " + adminUsername);
        }
    }

    public AuthenticationService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, UserMapper userMapper, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
    }

    @Async
    public CompletableFuture<ResponseEntity<AuthenticationResponse>> register(AuthenticationRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new InvalidCredentialsException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Email already exists");
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new InvalidCredentialsException("Phone number already exists");
        }

        User user = userMapper.toUserFromAuthRequest(request);

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setHasslot(false);

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        user = userRepository.save(user);

//        String token = jwtService.generateToken(user);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new AuthenticationResponse(true)));
    }

    @Async
    public CompletableFuture<ResponseEntity<LoginResponse>> authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(new LoginResponse("Invalid username or password", null, null, null, null, null)));
        }

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new UserWasNotFoundException("User was not found : %s".formatted(request.getUsername()))
        );

        // TODO - check if there is a slot in database in user account

        String token = jwtService.generateToken(user);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getEmail(), user.getPhone(), user.getFirstName(), user.getLastName())));
    }

    public EmailResponse findEmailByUsername(String username) {
        User providedUser = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(username))
                );

        return new EmailResponse(providedUser.getEmail());
    }

    public Boolean findIfHasSlot(String username){
        User providedUser = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(username))
                );
        return providedUser.getHasslot();
    }

    public void changeTheSlotParamToTrue(String username){
        User providedUser = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(username))
                );

        providedUser.setHasslot(true);

        userRepository.save(providedUser);
    }

    public void changeTheSlotParamToFalse(String username){
        User providedUser = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(username))
                );

        providedUser.setHasslot(false);

        userRepository.save(providedUser);
    }

    @Async
    public CompletableFuture<ResponseEntity<BooleanResponse>> changePassword(PasswordDTO passwordDTO) {
        User providedUser = userRepository.findByUsername(passwordDTO.getUsername())
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(passwordDTO.getPassword()))
                );

        providedUser.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));

        userRepository.save(providedUser);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new BooleanResponse(true)));
    }

    @Async
    public CompletableFuture<ResponseEntity<BooleanResponse>> changeFirstname(FirstnameDTO firstnameDTO) {
        User providedUser = userRepository.findByUsername(firstnameDTO.getUsername())
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(firstnameDTO.getFirstName()))
                );
        providedUser.setFirstName(firstnameDTO.getFirstName());

        userRepository.save(providedUser);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new BooleanResponse(true)));
    }

    @Async
    public CompletableFuture<ResponseEntity<BooleanResponse>> changeLastname(LastnameDTO lastnameDTO) {
        User providedUser = userRepository.findByUsername(lastnameDTO.getUsername())
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(lastnameDTO.getLastName()))
                );

        providedUser.setLastName(lastnameDTO.getLastName());

        userRepository.save(providedUser);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new BooleanResponse(true)));
    }

    @Async
    public CompletableFuture<ResponseEntity<BooleanResponse>> changeEmail(EmailDTO emailDTO) {
        User providedUser = userRepository.findByUsername(emailDTO.getUsername())
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(emailDTO.getEmail()))
                );

        providedUser.setEmail(emailDTO.getEmail());

        userRepository.save(providedUser);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new BooleanResponse(true)));
    }

    @Async
    public CompletableFuture<ResponseEntity<BooleanResponse>> changePhone(PhoneDTO phoneDTO) {
        User providedUser = userRepository.findByUsername(phoneDTO.getUsername())
                .orElseThrow(
                        () -> new UserWasNotFoundException("User was not found : %s".formatted(phoneDTO.getPhone()))
                );

        providedUser.setPhone(phoneDTO.getPhone());

        userRepository.save(providedUser);

        return CompletableFuture.completedFuture(ResponseEntity.ok(new BooleanResponse(true)));
    }
}
