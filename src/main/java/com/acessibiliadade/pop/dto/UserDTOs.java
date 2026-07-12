package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTOs {

    public record UserResponse(
            UUID id,
            String name,
            String email,
            LocalDateTime createdAt,
            BigDecimal accountBalance
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getAccountBalance()
            );
        }
    }

    public record UpdateUserRequest(
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 100)
            String name,

            @NotBlank(message = "Email é obrigatório")
            @Email(message = "Email inválido")
            String email,

            @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
            String password
    ) {}
}
