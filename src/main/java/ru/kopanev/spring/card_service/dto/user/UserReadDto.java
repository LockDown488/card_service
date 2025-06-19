package ru.kopanev.spring.card_service.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.kopanev.spring.card_service.enums.Role;

import java.util.List;

@Data
public class UserReadDto {

    private Long id;
    private String email;
    private Role role;
    private List<Long> cardsIds;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
