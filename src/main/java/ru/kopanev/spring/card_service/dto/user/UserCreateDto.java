package ru.kopanev.spring.card_service.dto.user;

import lombok.Data;
import ru.kopanev.spring.card_service.enums.Role;

@Data
public class UserCreateDto {

    private String email;
    private String password;
    private Role role;
}
