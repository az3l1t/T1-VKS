package net.az3l1t.authentication_server.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
@Schema(description = "Сущность пользователя : Логин")
public class LoginRequest {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 50, message = "Username should be between 4 and 50 characters")
    @Column(unique = true, nullable = false)
    @Schema(description = "min = 4, max = 50 : Не может быть пустым. Уникальный")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    @Column(nullable = false)
    @Schema(description = "min = 6 : Не может быть пустым")
    private String password;
}
