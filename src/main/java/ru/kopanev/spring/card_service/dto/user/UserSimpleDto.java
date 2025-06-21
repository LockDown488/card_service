package ru.kopanev.spring.card_service.dto.user;

import ru.kopanev.spring.card_service.enums.Role;

public record UserSimpleDto(Long id, String email, Role role) {
}
