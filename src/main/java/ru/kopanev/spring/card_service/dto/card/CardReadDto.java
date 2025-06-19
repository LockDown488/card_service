package ru.kopanev.spring.card_service.dto.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.transaction.TransactionDto;
import ru.kopanev.spring.card_service.dto.user.UserReadDto;
import ru.kopanev.spring.card_service.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CardReadDto {

    private Long id;
    private String cardNumber;
    private UserReadDto user;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private List<TransactionDto> transactions;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
