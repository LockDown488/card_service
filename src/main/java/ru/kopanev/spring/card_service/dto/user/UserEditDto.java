package ru.kopanev.spring.card_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.card.CardSimpleDto;
import ru.kopanev.spring.card_service.enums.Role;

import java.util.List;

@Data
@Builder
public class UserEditDto {

    @NotNull(message = "ID обязателен для редактирования")
    private Long id;

    @Email
    private String email;

    private String password;
    private Role role;
    private List<CardSimpleDto> cards;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
