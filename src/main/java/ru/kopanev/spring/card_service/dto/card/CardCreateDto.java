package ru.kopanev.spring.card_service.dto.card;

import lombok.Data;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.enums.CardStatus;

import java.time.LocalDate;

@Data
public class CardCreateDto {

    private String cardNumber;
    private UserReadDto user;
    private LocalDate expiryDate;
    private CardStatus status;

}
