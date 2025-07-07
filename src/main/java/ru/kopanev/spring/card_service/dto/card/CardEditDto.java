package ru.kopanev.spring.card_service.dto.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.user.UserSimpleDto;
import ru.kopanev.spring.card_service.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardEditDto {

    private Long id;
    private String cardNumber;
    private UserSimpleDto user;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal dailyLimit;
    private Boolean isBlockRequested;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
