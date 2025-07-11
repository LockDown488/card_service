package ru.kopanev.spring.card_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.card.CardSimpleDto;
import ru.kopanev.spring.card_service.enums.Role;

import java.util.List;

@Data
@Builder
public class UserEditDto {

    private Long id;
    private String email;
    private String password;
    private Role role;
    private List<CardSimpleDto> cards;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
