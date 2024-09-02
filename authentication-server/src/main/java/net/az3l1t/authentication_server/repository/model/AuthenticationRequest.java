package net.az3l1t.authentication_server.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность пользователя : Регистрация")
public class AuthenticationRequest {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 50, message = "Username should be between 4 and 50 characters")
    @Schema(description = "min = 4, max = 50 : Не может быть пустым. Уникальный")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    @Schema(description = "min = 6 : Не может быть пустым")
    private String password;

    @Schema(description = "Все правила эмэйла. Уникальный. Не может быть пустым")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    @Schema(description = "Не может быть пустым")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    @Schema(description = "Не может быть пустым")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Size(min = 10, max = 15, message = "Phone number should be between 10 and 15 characters")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Phone number can only contain digits and an optional leading '+'")
    @Schema(description = "Phone number should be valid and unique")
    private String phone;
}
