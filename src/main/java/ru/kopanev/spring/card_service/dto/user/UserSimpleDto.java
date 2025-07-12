package ru.kopanev.spring.card_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.kopanev.spring.card_service.enums.Role;

public record UserSimpleDto(@NotNull Long id, @Email @NotBlank String email, @NotBlank Role role) {
}
