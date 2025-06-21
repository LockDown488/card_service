package ru.kopanev.spring.card_service.filter;

import lombok.Data;
import ru.kopanev.spring.card_service.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardFilter {

    private Long userId;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal dailyLimit;

}
