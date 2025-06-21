package ru.kopanev.spring.card_service.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.kopanev.spring.card_service.dto.card.CardSimpleDto;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionDto {

    private Long id;
    private CardSimpleDto card;
    private BigDecimal amount;
    private LocalDate timestamp;

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
}
